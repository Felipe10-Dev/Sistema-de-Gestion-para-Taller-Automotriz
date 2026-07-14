package com.serviteca.compra.service;

import com.serviteca.compra.dto.*;
import com.serviteca.compra.entity.OrdenCompra;
import com.serviteca.compra.entity.OrdenCompraProducto;
import com.serviteca.compra.repository.OrdenCompraProductoRepository;
import com.serviteca.compra.repository.OrdenCompraRepository;
import com.serviteca.inventario.entity.Inventario;
import com.serviteca.inventario.entity.MovimientoInventario;
import com.serviteca.inventario.repository.InventarioRepository;
import com.serviteca.inventario.repository.MovimientoInventarioRepository;
import com.serviteca.producto.entity.Producto;
import com.serviteca.producto.repository.ProductoRepository;
import com.serviteca.proveedor.entity.Proveedor;
import com.serviteca.proveedor.repository.ProveedorRepository;
import com.serviteca.shared.exception.BadRequestException;
import com.serviteca.shared.exception.DuplicateResourceException;
import com.serviteca.shared.exception.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrdenCompraService {

    private final OrdenCompraRepository ordenRepository;
    private final OrdenCompraProductoRepository productoRepository;
    private final ProductoRepository productoRepo;
    private final ProveedorRepository proveedorRepository;
    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    public OrdenCompraService(OrdenCompraRepository ordenRepository,
                              OrdenCompraProductoRepository productoRepository,
                              ProductoRepository productoRepo,
                              ProveedorRepository proveedorRepository,
                              InventarioRepository inventarioRepository,
                              MovimientoInventarioRepository movimientoRepository) {
        this.ordenRepository = ordenRepository;
        this.productoRepository = productoRepository;
        this.productoRepo = productoRepo;
        this.proveedorRepository = proveedorRepository;
        this.inventarioRepository = inventarioRepository;
        this.movimientoRepository = movimientoRepository;
    }

    public List<OrdenCompraResponse> findAll() {
        List<OrdenCompra> ordenes = ordenRepository.findAll();
        ordenes.sort((a, b) -> b.getFecha().compareTo(a.getFecha()));
        return ordenes.stream().map(this::toFullResponse).toList();
    }

    public OrdenCompraResponse findById(Long id) {
        OrdenCompra orden = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra", id));
        return toFullResponse(orden);
    }

    @Transactional
    public OrdenCompraResponse create(OrdenCompraRequest request) {
        OrdenCompra orden = new OrdenCompra();
        orden.setNumeroOrden(generarNumeroOrden());
        orden.setEstado("BORRADOR");

        if (request.getProveedorId() == null) {
            throw new BadRequestException("La orden de compra debe tener un proveedor asignado");
        }
        orden.setProveedor(proveedorRepository.findById(request.getProveedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", request.getProveedorId())));
        orden.setObservaciones(request.getObservaciones());
        orden = ordenRepository.save(orden);

        List<Long> productoIds = request.getProductos().stream()
                .map(OrdenCompraRequest.ProductoCompraItem::getProductoId)
                .toList();
        Map<Long, Producto> productosMap = productoRepo.findAllById(productoIds).stream()
                .collect(Collectors.toMap(Producto::getId, p -> p));

        BigDecimal total = BigDecimal.ZERO;
        for (OrdenCompraRequest.ProductoCompraItem item : request.getProductos()) {
            Producto producto = productosMap.get(item.getProductoId());
            if (producto == null) {
                throw new ResourceNotFoundException("Producto", item.getProductoId());
            }

            OrdenCompraProducto op = new OrdenCompraProducto();
            op.setOrdenCompra(orden);
            op.setProducto(producto);
            op.setCantidad(item.getCantidad());
            op.setPrecioUnitario(item.getPrecioUnitario() != null ? item.getPrecioUnitario() : BigDecimal.ZERO);
            op.setSubtotal(op.getPrecioUnitario().multiply(BigDecimal.valueOf(op.getCantidad())));
            productoRepository.save(op);
            total = total.add(op.getSubtotal());
        }

        orden.setTotal(total);
        orden = ordenRepository.save(orden);
        return toFullResponse(orden);
    }

    @Transactional
    public OrdenCompraResponse cambiarEstado(Long id, CambioEstadoCompraRequest request) {
        OrdenCompra orden = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra", id));

        String estadoActual = orden.getEstado();
        String nuevoEstado = request.getEstado();

        validarTransicion(estadoActual, nuevoEstado);

        if (nuevoEstado.equals("RECIBIDA")) {
            recibirMercancia(orden);
        }

        orden.setEstado(nuevoEstado);
        orden = ordenRepository.save(orden);
        return toFullResponse(orden);
    }

    private void recibirMercancia(OrdenCompra orden) {
        List<OrdenCompraProducto> productos = productoRepository.findByOrdenCompraId(orden.getId());
        String usuario = obtenerUsuarioActual();

        List<Long> productoIds = productos.stream()
                .map(op -> op.getProducto().getId())
                .toList();
        Map<Long, Inventario> inventarioMap = inventarioRepository.findByProductoIdIn(productoIds).stream()
                .collect(Collectors.toMap(i -> i.getProducto().getId(), i -> i));
        Set<Long> procesados = new HashSet<>();

        for (OrdenCompraProducto op : productos) {
            Long prodId = op.getProducto().getId();
            Inventario inv = inventarioMap.get(prodId);

            if (inv != null) {
                inv.setCantidadActual(inv.getCantidadActual() + op.getCantidad());
                inventarioRepository.save(inv);
                procesados.add(prodId);
            }

            MovimientoInventario mov = new MovimientoInventario();
            mov.setProducto(op.getProducto());
            mov.setTipoMovimiento("ENTRADA");
            mov.setCantidad(op.getCantidad());
            mov.setMotivo("Orden de Compra #" + orden.getNumeroOrden());
            mov.setUsuario(usuario);
            movimientoRepository.save(mov);
        }
    }

    public void delete(Long id, String motivo) {
        OrdenCompra orden = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra", id));
        if (!orden.getEstado().equals("BORRADOR")) {
            throw new BadRequestException("S\u00f3lo se pueden eliminar \u00f3rdenes en estado BORRADOR");
        }
        orden.marcarComoEliminado(obtenerUsuarioActual(), motivo);
        ordenRepository.save(orden);
    }

    public List<RecomendacionCompraResponse> obtenerRecomendaciones() {
        List<Producto> productos = productoRepo.findAll().stream()
                .filter(p -> p.isActivo())
                .toList();
        if (productos.isEmpty()) return List.of();

        List<Long> productoIds = productos.stream().map(Producto::getId).toList();
        Map<Long, Inventario> inventarioMap = inventarioRepository.findByProductoIdIn(productoIds).stream()
                .collect(Collectors.toMap(i -> i.getProducto().getId(), i -> i));

        List<RecomendacionCompraResponse> recomendaciones = new ArrayList<>();

        for (Producto p : productos) {
            Inventario inv = inventarioMap.get(p.getId());
            if (inv == null) continue;

            int stockActual = inv.getCantidadActual();
            int puntoRep = p.getPuntoReposicion() != null ? p.getPuntoReposicion() : (p.getStockMinimo() != null ? p.getStockMinimo() : 0);

            if (stockActual > puntoRep && stockActual > 0) continue;

            RecomendacionCompraResponse r = new RecomendacionCompraResponse();
            r.setProductoId(p.getId());
            r.setProductoCodigo(p.getCodigo());
            r.setProductoNombre(p.getNombre());
            r.setStockActual(stockActual);
            r.setStockMinimo(p.getStockMinimo());
            r.setStockMaximo(p.getStockMaximo());
            r.setPuntoReposicion(puntoRep);

            int maximo = p.getStockMaximo() != null ? p.getStockMaximo() : (p.getStockMinimo() != null ? p.getStockMinimo() * 2 : 10);
            int sugerida = Math.max(maximo - stockActual, 1);
            r.setCantidadSugerida(sugerida);

            if (p.getProveedor() != null) {
                r.setUltimoProveedor(p.getProveedor().getNombre());
            }
            r.setUltimoPrecioCompra(p.getPrecioCompra());

            if (stockActual == 0) r.setAlerta("AGOTADO");
            else if (stockActual <= puntoRep) r.setAlerta("REPONER");
            else r.setAlerta("OK");

            recomendaciones.add(r);
        }

        recomendaciones.sort(Comparator.comparing(RecomendacionCompraResponse::getAlerta));
        return recomendaciones;
    }

    public List<HistorialCompraProductoResponse> obtenerHistorialProducto(Long productoId) {
        productoRepo.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", productoId));

        List<OrdenCompraProducto> items = productoRepository.findByProductoIdOrderByOrdenCompraFechaDesc(productoId);
        return items.stream().map(op -> {
            HistorialCompraProductoResponse h = new HistorialCompraProductoResponse();
            h.setOrdenCompraId(op.getOrdenCompra().getId());
            h.setNumeroOrden(op.getOrdenCompra().getNumeroOrden());
            if (op.getOrdenCompra().getProveedor() != null) {
                h.setProveedorId(op.getOrdenCompra().getProveedor().getId());
                h.setProveedorNombre(op.getOrdenCompra().getProveedor().getNombre());
            }
            h.setFecha(op.getOrdenCompra().getFecha());
            h.setCantidad(op.getCantidad());
            h.setPrecioUnitario(op.getPrecioUnitario());
            h.setSubtotal(op.getSubtotal());
            h.setEstado(op.getOrdenCompra().getEstado());
            return h;
        }).toList();
    }

    public EstadisticasProveedorResponse obtenerEstadisticasProveedor(Long proveedorId) {
        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", proveedorId));

        EstadisticasProveedorResponse stats = new EstadisticasProveedorResponse();
        stats.setProveedorId(proveedor.getId());
        stats.setProveedorNombre(proveedor.getNombre());
        stats.setTotalComprado(ordenRepository.sumTotalByProveedorIdAndEstadoRecibida(proveedorId));
        stats.setNumeroOrdenes(ordenRepository.countByProveedorIdAndEstado(proveedorId, "RECIBIDA"));

        LocalDateTime ultima = ordenRepository.findMaxFechaByProveedorIdAndEstadoRecibida(proveedorId);
        stats.setUltimaCompra(ultima);

        List<Long> productoIds = productoRepository.findProductoIdsByProveedorId(proveedorId);
        stats.setProductosSuministrados(productoIds.size());

        stats.setPromedioDiasEntrega(calcularPromedioDiasEntrega(proveedorId));

        return stats;
    }

    private double calcularPromedioDiasEntrega(Long proveedorId) {
        return ordenRepository.findPromedioDiasEntregaByProveedorId(proveedorId)
                .map(r -> ((Number) r[1]).doubleValue())
                .orElse(0.0);
    }

    private void validarTransicion(String actual, String nuevo) {
        Map<String, List<String>> transiciones = Map.of(
                "BORRADOR", List.of("ENVIADA", "CANCELADA"),
                "ENVIADA", List.of("RECIBIDA", "CANCELADA"),
                "RECIBIDA", List.of(),
                "CANCELADA", List.of()
        );
        List<String> permitidos = transiciones.get(actual);
        if (permitidos == null || !permitidos.contains(nuevo)) {
            throw new BadRequestException("No se puede cambiar de " + actual + " a " + nuevo);
        }
    }

    private OrdenCompraResponse toFullResponse(OrdenCompra orden) {
        OrdenCompraResponse res = new OrdenCompraResponse();
        res.setId(orden.getId());
        res.setNumeroOrden(orden.getNumeroOrden());
        if (orden.getProveedor() != null) {
            res.setProveedorId(orden.getProveedor().getId());
            res.setProveedorNombre(orden.getProveedor().getNombre());
        }
        res.setFecha(orden.getFecha());
        res.setEstado(orden.getEstado());
        res.setTotal(orden.getTotal());
        res.setObservaciones(orden.getObservaciones());

        List<OrdenCompraProducto> items = productoRepository.findByOrdenCompraId(orden.getId());
        res.setProductos(items.stream().map(op -> {
            OrdenCompraResponse.ProductoResponse pr = new OrdenCompraResponse.ProductoResponse();
            pr.setId(op.getId());
            pr.setProductoId(op.getProducto().getId());
            pr.setProductoCodigo(op.getProducto().getCodigo());
            pr.setProductoNombre(op.getProducto().getNombre());
            pr.setCantidad(op.getCantidad());
            pr.setPrecioUnitario(op.getPrecioUnitario());
            pr.setSubtotal(op.getSubtotal());
            return pr;
        }).toList());

        return res;
    }

    private String generarNumeroOrden() {
        return "OC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String obtenerUsuarioActual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SISTEMA";
    }
}