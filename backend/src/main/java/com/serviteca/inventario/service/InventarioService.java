package com.serviteca.inventario.service;

import com.serviteca.inventario.dto.InventarioRequest;
import com.serviteca.inventario.dto.InventarioResponse;
import com.serviteca.inventario.dto.MovimientoRequest;
import com.serviteca.inventario.dto.MovimientoResponse;
import com.serviteca.inventario.entity.Inventario;
import com.serviteca.inventario.entity.MovimientoInventario;
import com.serviteca.inventario.mapper.InventarioMapper;
import com.serviteca.inventario.repository.InventarioRepository;
import com.serviteca.inventario.repository.MovimientoInventarioRepository;
import com.serviteca.inventario.validator.InventarioValidator;
import com.serviteca.producto.entity.Producto;
import com.serviteca.producto.repository.ProductoRepository;
import com.serviteca.shared.dto.PagedResponse;
import com.serviteca.shared.exception.BadRequestException;
import com.serviteca.shared.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final InventarioMapper inventarioMapper;
    private final InventarioValidator inventarioValidator;

    public InventarioService(InventarioRepository inventarioRepository,
                             MovimientoInventarioRepository movimientoRepository,
                             ProductoRepository productoRepository,
                             InventarioMapper inventarioMapper,
                             InventarioValidator inventarioValidator) {
        this.inventarioRepository = inventarioRepository;
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
        this.inventarioMapper = inventarioMapper;
        this.inventarioValidator = inventarioValidator;
    }

    public PagedResponse<InventarioResponse> findAll(int page, int size, String sort) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sort != null ? sort : "id"));
        Page<Inventario> inventarios = inventarioRepository.findAll(pageRequest);
        return toPagedResponse(inventarios);
    }

    public List<InventarioResponse> findAllSimple() {
        return inventarioRepository.findAll(Sort.by("id")).stream()
                .map(inventarioMapper::toResponse)
                .toList();
    }

    public InventarioResponse findById(Long id) {
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", id));
        return inventarioMapper.toResponse(inventario);
    }

    public InventarioResponse findByProductoId(Long productoId) {
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario para el producto", productoId));
        return inventarioMapper.toResponse(inventario);
    }

    public List<InventarioResponse> findBajoStock() {
        return inventarioRepository.findProductosBajoStock().stream()
                .map(inventarioMapper::toResponse)
                .toList();
    }

    public InventarioResponse create(InventarioRequest request) {
        if (inventarioRepository.findByProductoId(request.getProductoId()).isPresent()) {
            throw new BadRequestException("El producto ya tiene un registro en inventario");
        }

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", request.getProductoId()));

        Inventario inventario = new Inventario();
        inventario.setProducto(producto);
        inventario.setCantidadActual(0);
        inventario.setCantidadMinima(request.getCantidadMinima());
        inventario.setUbicacion(request.getUbicacion());

        inventario = inventarioRepository.save(inventario);
        return inventarioMapper.toResponse(inventario);
    }

    @Transactional
    public MovimientoResponse registrarMovimiento(MovimientoRequest request) {
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", request.getProductoId()));

        Inventario inventario = inventarioRepository.findByProductoId(request.getProductoId())
                .orElseGet(() -> {
                    Inventario nuevo = new Inventario();
                    nuevo.setProducto(producto);
                    nuevo.setCantidadActual(0);
                    nuevo.setCantidadMinima(0);
                    return inventarioRepository.save(nuevo);
                });

        inventarioValidator.validateMovimiento(request.getTipoMovimiento(),
                inventario.getCantidadActual(), request.getCantidad());

        if (request.getTipoMovimiento().equals("ENTRADA")) {
            inventario.setCantidadActual(inventario.getCantidadActual() + request.getCantidad());
        } else {
            inventario.setCantidadActual(inventario.getCantidadActual() - request.getCantidad());
        }

        inventarioRepository.save(inventario);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setTipoMovimiento(request.getTipoMovimiento());
        movimiento.setCantidad(request.getCantidad());
        movimiento.setMotivo(request.getMotivo());
        movimiento.setUsuario(SecurityContextHolder.getContext().getAuthentication().getName());

        movimiento = movimientoRepository.save(movimiento);
        return inventarioMapper.toMovimientoResponse(movimiento);
    }

    public List<MovimientoResponse> getMovimientos(Long productoId) {
        return movimientoRepository.findByProductoIdOrderByFechaMovimientoDesc(productoId).stream()
                .map(inventarioMapper::toMovimientoResponse)
                .toList();
    }

    public InventarioResponse update(Long id, InventarioRequest request) {
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", id));

        inventario.setCantidadMinima(request.getCantidadMinima());
        inventario.setUbicacion(request.getUbicacion());

        inventario = inventarioRepository.save(inventario);
        return inventarioMapper.toResponse(inventario);
    }

    private PagedResponse<InventarioResponse> toPagedResponse(Page<Inventario> page) {
        return new PagedResponse<>(
                page.getContent().stream().map(inventarioMapper::toResponse).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(),
                page.getTotalPages(), page.isLast()
        );
    }
}
