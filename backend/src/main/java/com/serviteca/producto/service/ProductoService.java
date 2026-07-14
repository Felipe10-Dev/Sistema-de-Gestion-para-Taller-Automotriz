package com.serviteca.producto.service;

import com.serviteca.categoria.entity.Categoria;
import com.serviteca.categoria.repository.CategoriaRepository;
import com.serviteca.producto.dto.ProductoRequest;
import com.serviteca.producto.dto.ProductoResponse;
import com.serviteca.producto.entity.Producto;
import com.serviteca.producto.mapper.ProductoMapper;
import com.serviteca.producto.repository.ProductoRepository;
import com.serviteca.proveedor.entity.Proveedor;
import com.serviteca.proveedor.repository.ProveedorRepository;
import com.serviteca.shared.dto.PagedResponse;
import com.serviteca.shared.exception.BadRequestException;
import com.serviteca.shared.exception.DuplicateResourceException;
import com.serviteca.shared.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoMapper productoMapper;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository,
                           ProveedorRepository proveedorRepository, ProductoMapper productoMapper) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.proveedorRepository = proveedorRepository;
        this.productoMapper = productoMapper;
    }

    public PagedResponse<ProductoResponse> findAll(int page, int size, String sort, String search,
                                                    Long categoriaId, Long proveedorId,
                                                    boolean bajoStock, boolean sinStock) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sort != null ? sort : "nombre"));
        Page<Producto> productos;

        if (search != null && !search.isBlank()) {
            productos = productoRepository.search(search, pageRequest);
        } else if (bajoStock) {
            productos = productoRepository.findBajoStock(pageRequest);
        } else if (sinStock) {
            productos = productoRepository.findSinStock(pageRequest);
        } else if (categoriaId != null || proveedorId != null) {
            productos = productoRepository.findByFilters(categoriaId, proveedorId, pageRequest);
        } else {
            productos = productoRepository.findByActivoTrue(pageRequest);
        }

        return toPagedResponse(productos);
    }

    public List<ProductoResponse> findAllSimple(String search) {
        List<Producto> productos;
        if (search != null && !search.isBlank()) {
            productos = productoRepository.search(search, Pageable.unpaged()).getContent();
        } else {
            productos = productoRepository.findAll();
        }
        return productos.stream().map(productoMapper::toResponse).toList();
    }

    public ProductoResponse findById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        return productoMapper.toResponse(producto);
    }

    public ProductoResponse create(ProductoRequest request) {
        if (productoRepository.existsByCodigo(request.getCodigo())) {
            throw new DuplicateResourceException("Ya existe un producto con el c\u00f3digo " + request.getCodigo());
        }

        Producto producto = new Producto();
        producto.setCodigo(request.getCodigo());
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        if (request.getPrecioVenta().compareTo(request.getPrecioCompra()) < 0) {
            throw new BadRequestException("El precio de venta no puede ser menor que el precio de compra");
        }
        producto.setPrecioCompra(request.getPrecioCompra());
        producto.setPrecioVenta(request.getPrecioVenta());
        producto.setStockMinimo(request.getStockMinimo());
        producto.setStockMaximo(request.getStockMaximo());
        producto.setPuntoReposicion(request.getPuntoReposicion());

        if (request.getCategoriaId() != null) {
            producto.setCategoria(categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categor\u00eda", request.getCategoriaId())));
        }
        if (request.getProveedorId() != null) {
            producto.setProveedor(proveedorRepository.findById(request.getProveedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor", request.getProveedorId())));
        }

        producto = productoRepository.save(producto);
        return productoMapper.toResponse(producto);
    }

    public ProductoResponse update(Long id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));

        productoRepository.findByCodigo(request.getCodigo()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new DuplicateResourceException("Ya existe otro producto con el c\u00f3digo " + request.getCodigo());
            }
        });

        producto.setCodigo(request.getCodigo());
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        if (request.getPrecioVenta().compareTo(request.getPrecioCompra()) < 0) {
            throw new BadRequestException("El precio de venta no puede ser menor que el precio de compra");
        }
        producto.setPrecioCompra(request.getPrecioCompra());
        producto.setPrecioVenta(request.getPrecioVenta());
        producto.setStockMinimo(request.getStockMinimo());
        producto.setStockMaximo(request.getStockMaximo());
        producto.setPuntoReposicion(request.getPuntoReposicion());

        if (request.getCategoriaId() != null) {
            producto.setCategoria(categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categor\u00eda", request.getCategoriaId())));
        } else {
            producto.setCategoria(null);
        }

        if (request.getProveedorId() != null) {
            producto.setProveedor(proveedorRepository.findById(request.getProveedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor", request.getProveedorId())));
        } else {
            producto.setProveedor(null);
        }

        producto = productoRepository.save(producto);
        return productoMapper.toResponse(producto);
    }

    public void delete(Long id, String motivo) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        producto.marcarComoEliminado(obtenerUsuarioActual(), motivo);
        productoRepository.save(producto);
    }

    private String obtenerUsuarioActual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SISTEMA";
    }

    private PagedResponse<ProductoResponse> toPagedResponse(Page<Producto> page) {
        return new PagedResponse<>(
                page.getContent().stream().map(productoMapper::toResponse).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(),
                page.getTotalPages(), page.isLast()
        );
    }
}
