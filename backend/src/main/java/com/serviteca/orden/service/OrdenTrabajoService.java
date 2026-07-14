package com.serviteca.orden.service;

import com.serviteca.cliente.entity.Cliente;
import com.serviteca.cliente.repository.ClienteRepository;
import com.serviteca.inventario.entity.Inventario;
import com.serviteca.inventario.entity.MovimientoInventario;
import com.serviteca.inventario.repository.InventarioRepository;
import com.serviteca.inventario.repository.MovimientoInventarioRepository;
import com.serviteca.orden.dto.CambioEstadoRequest;
import com.serviteca.orden.dto.ObservacionRequest;
import com.serviteca.orden.dto.OrdenTrabajoRequest;
import com.serviteca.orden.dto.OrdenTrabajoResponse;
import com.serviteca.orden.entity.HistorialEstado;
import com.serviteca.orden.entity.OrdenObservacion;
import com.serviteca.orden.entity.OrdenProducto;
import com.serviteca.orden.entity.OrdenServicio;
import com.serviteca.orden.entity.OrdenTrabajo;
import com.serviteca.orden.enums.OrdenEstado;
import com.serviteca.orden.mapper.OrdenMapper;
import com.serviteca.orden.repository.HistorialEstadoRepository;
import com.serviteca.orden.repository.OrdenObservacionRepository;
import com.serviteca.orden.repository.OrdenProductoRepository;
import com.serviteca.orden.repository.OrdenServicioRepository;
import com.serviteca.orden.repository.OrdenTrabajoRepository;
import com.serviteca.orden.validator.OrdenValidator;
import com.serviteca.producto.entity.Producto;
import com.serviteca.producto.repository.ProductoRepository;
import com.serviteca.servicio.entity.Servicio;
import com.serviteca.servicio.repository.ServicioRepository;
import com.serviteca.shared.dto.PagedResponse;
import com.serviteca.shared.exception.BadRequestException;
import com.serviteca.shared.exception.ResourceNotFoundException;
import com.serviteca.usuario.entity.Usuario;
import com.serviteca.usuario.repository.UsuarioRepository;
import com.serviteca.vehiculo.entity.Vehiculo;
import com.serviteca.vehiculo.repository.VehiculoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrdenTrabajoService {

    private final OrdenTrabajoRepository ordenRepository;
    private final OrdenServicioRepository ordenServicioRepository;
    private final OrdenProductoRepository ordenProductoRepository;
    private final HistorialEstadoRepository historialRepository;
    private final OrdenObservacionRepository observacionRepository;
    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    private final ServicioRepository servicioRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final OrdenMapper ordenMapper;
    private final OrdenValidator ordenValidator;

    public OrdenTrabajoService(OrdenTrabajoRepository ordenRepository,
                               OrdenServicioRepository ordenServicioRepository,
                               OrdenProductoRepository ordenProductoRepository,
                               HistorialEstadoRepository historialRepository,
                               OrdenObservacionRepository observacionRepository,
                               ClienteRepository clienteRepository,
                               VehiculoRepository vehiculoRepository,
                               ServicioRepository servicioRepository,
                               ProductoRepository productoRepository,
                               UsuarioRepository usuarioRepository,
                               InventarioRepository inventarioRepository,
                               MovimientoInventarioRepository movimientoRepository,
                               OrdenMapper ordenMapper, OrdenValidator ordenValidator) {
        this.ordenRepository = ordenRepository;
        this.ordenServicioRepository = ordenServicioRepository;
        this.ordenProductoRepository = ordenProductoRepository;
        this.historialRepository = historialRepository;
        this.observacionRepository = observacionRepository;
        this.clienteRepository = clienteRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.servicioRepository = servicioRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.inventarioRepository = inventarioRepository;
        this.movimientoRepository = movimientoRepository;
        this.ordenMapper = ordenMapper;
        this.ordenValidator = ordenValidator;
    }

    public PagedResponse<OrdenTrabajoResponse> findAll(int page, int size, String sort, String search,
                                                       String estado, String estadoFinanciero,
                                                       Long clienteId, Long vehiculoId,
                                                       LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));
        Page<OrdenTrabajo> ordenes;

        if (search != null && !search.isBlank()) {
            ordenes = ordenRepository.search(search, pageRequest);
        } else if (estado != null && !estado.isBlank()) {
            ordenes = ordenRepository.findByEstado(estado, pageRequest);
        } else if (estadoFinanciero != null || clienteId != null || vehiculoId != null ||
                   fechaInicio != null || fechaFin != null) {
            ordenes = ordenRepository.findByFilters(null, estadoFinanciero, clienteId,
                     vehiculoId, fechaInicio, fechaFin, pageRequest);
        } else {
            ordenes = ordenRepository.findActivas(pageRequest);
        }

        return toPagedResponse(ordenes);
    }

    public OrdenTrabajoResponse findById(Long id) {
        OrdenTrabajo orden = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de trabajo", id));
        List<OrdenServicio> servicios = ordenServicioRepository.findByOrdenId(id);
        List<OrdenProducto> productos = ordenProductoRepository.findByOrdenId(id);
        List<HistorialEstado> historial = historialRepository.findByOrdenIdOrderByFechaAsc(id);
        List<OrdenObservacion> observaciones = observacionRepository.findByOrdenIdOrderByFechaAsc(id);
        return ordenMapper.toResponse(orden, servicios, productos, historial, observaciones);
    }

    @Transactional
    public OrdenTrabajoResponse create(OrdenTrabajoRequest request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.getClienteId()));

        Vehiculo vehiculo = vehiculoRepository.findById(request.getVehiculoId())
                .orElseThrow(() -> new ResourceNotFoundException("Veh\u00edculo", request.getVehiculoId()));

        OrdenTrabajo orden = new OrdenTrabajo();
        orden.setNumeroOrden(generarNumeroOrden());
        orden.setCliente(cliente);
        orden.setVehiculo(vehiculo);
        orden.setKilometraje(request.getKilometraje());
        orden.setEstado(OrdenEstado.PENDIENTE.name());
        orden.setEstadoFinanciero("SIN_PAGAR");
        orden.setObservaciones(request.getObservaciones());

        if (request.getTecnicoId() != null) {
            Usuario tecnico = usuarioRepository.findById(request.getTecnicoId())
                    .orElseThrow(() -> new ResourceNotFoundException("T\u00e9cnico", request.getTecnicoId()));
            orden.setTecnico(tecnico);
        }

        orden = ordenRepository.save(orden);

        BigDecimal totalServicios = BigDecimal.ZERO;
        if (request.getServicios() != null && !request.getServicios().isEmpty()) {
            List<Long> servicioIds = request.getServicios().stream()
                    .map(OrdenTrabajoRequest.ServicioItem::getServicioId)
                    .toList();
            Map<Long, Servicio> serviciosMap = servicioRepository.findAllById(servicioIds).stream()
                    .collect(Collectors.toMap(Servicio::getId, s -> s));

            for (OrdenTrabajoRequest.ServicioItem item : request.getServicios()) {
                Servicio servicio = serviciosMap.get(item.getServicioId());
                if (servicio == null) {
                    throw new ResourceNotFoundException("Servicio", item.getServicioId());
                }

                OrdenServicio os = new OrdenServicio();
                os.setOrden(orden);
                os.setServicio(servicio);
                os.setCantidad(item.getCantidad() != null ? item.getCantidad() : 1);
                os.setPrecioUnitario(servicio.getPrecio());
                os.setSubtotal(servicio.getPrecio().multiply(BigDecimal.valueOf(os.getCantidad())));
                os.setObservaciones(item.getObservaciones());
                ordenServicioRepository.save(os);
                totalServicios = totalServicios.add(os.getSubtotal());
            }
        }

        BigDecimal totalProductos = BigDecimal.ZERO;
        if (request.getProductos() != null && !request.getProductos().isEmpty()) {
            List<Long> productoIds = request.getProductos().stream()
                    .map(OrdenTrabajoRequest.ProductoItem::getProductoId)
                    .toList();
            Map<Long, Producto> productosMap = productoRepository.findAllById(productoIds).stream()
                    .collect(Collectors.toMap(Producto::getId, p -> p));

            for (OrdenTrabajoRequest.ProductoItem item : request.getProductos()) {
                Producto producto = productosMap.get(item.getProductoId());
                if (producto == null) {
                    throw new ResourceNotFoundException("Producto", item.getProductoId());
                }

                int cantidad = item.getCantidad() != null ? item.getCantidad() : 1;
                validarStockDisponible(producto.getId(), cantidad);

                OrdenProducto op = new OrdenProducto();
                op.setOrden(orden);
                op.setProducto(producto);
                op.setCantidad(cantidad);
                op.setPrecioUnitario(producto.getPrecioVenta());
                op.setSubtotal(producto.getPrecioVenta().multiply(BigDecimal.valueOf(cantidad)));
                ordenProductoRepository.save(op);
                totalProductos = totalProductos.add(op.getSubtotal());
            }
        }

        actualizarTotales(orden, totalServicios, totalProductos);

        return buildFullResponse(orden);
    }

    @Transactional
    public OrdenTrabajoResponse cambiarEstado(Long id, CambioEstadoRequest request) {
        OrdenTrabajo orden = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de trabajo", id));

        ordenValidator.validarEstado(request.getEstado());
        ordenValidator.validarTransicion(orden.getEstado(), request.getEstado());

        String estadoAnterior = orden.getEstado();
        String nuevoEstado = request.getEstado();

        orden.setEstado(nuevoEstado);

        if (nuevoEstado.equals(OrdenEstado.LISTO_PARA_ENTREGA.name())) {
            ordenValidator.validarParaEntrega(orden);
        }

        if (nuevoEstado.equals(OrdenEstado.ENTREGADO.name())) {
            orden.setFechaSalida(LocalDateTime.now());
            ordenValidator.validarParaEntrega(orden);
            descontarInventario(orden);
        }

        if (request.getObservaciones() != null) {
            orden.setObservaciones(request.getObservaciones());
        }

        orden = ordenRepository.save(orden);

        registrarHistorial(orden, estadoAnterior, nuevoEstado, request.getObservaciones());

        return buildFullResponse(orden);
    }

    @Transactional
    public OrdenTrabajoResponse actualizarOrden(Long id, OrdenTrabajoRequest request) {
        OrdenTrabajo orden = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de trabajo", id));
        ordenValidator.validarTerminal(orden.getEstado());

        if (request.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.getClienteId()));
            orden.setCliente(cliente);
        }
        if (request.getVehiculoId() != null) {
            Vehiculo vehiculo = vehiculoRepository.findById(request.getVehiculoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Veh\u00edculo", request.getVehiculoId()));
            orden.setVehiculo(vehiculo);
        }
        if (request.getKilometraje() != null) {
            orden.setKilometraje(request.getKilometraje());
        }
        if (request.getTecnicoId() != null) {
            Usuario tecnico = usuarioRepository.findById(request.getTecnicoId())
                    .orElseThrow(() -> new ResourceNotFoundException("T\u00e9cnico", request.getTecnicoId()));
            orden.setTecnico(tecnico);
        }
        if (request.getObservaciones() != null) {
            orden.setObservaciones(request.getObservaciones());
        }

        if (request.getServicios() != null) {
            ordenServicioRepository.deleteByOrdenId(id);
            BigDecimal totalServicios = BigDecimal.ZERO;

            if (!request.getServicios().isEmpty()) {
                List<Long> servicioIds = request.getServicios().stream()
                        .map(OrdenTrabajoRequest.ServicioItem::getServicioId)
                        .toList();
                Map<Long, Servicio> serviciosMap = servicioRepository.findAllById(servicioIds).stream()
                        .collect(Collectors.toMap(Servicio::getId, s -> s));

                for (OrdenTrabajoRequest.ServicioItem item : request.getServicios()) {
                    Servicio servicio = serviciosMap.get(item.getServicioId());
                    if (servicio == null) {
                        throw new ResourceNotFoundException("Servicio", item.getServicioId());
                    }
                    OrdenServicio os = new OrdenServicio();
                    os.setOrden(orden);
                    os.setServicio(servicio);
                    os.setCantidad(item.getCantidad() != null ? item.getCantidad() : 1);
                    os.setPrecioUnitario(servicio.getPrecio());
                    os.setSubtotal(servicio.getPrecio().multiply(BigDecimal.valueOf(os.getCantidad())));
                    os.setObservaciones(item.getObservaciones());
                    ordenServicioRepository.save(os);
                    totalServicios = totalServicios.add(os.getSubtotal());
                }
            }
            orden.setTotalServicios(totalServicios);
        }

        if (request.getProductos() != null) {
            ordenProductoRepository.deleteByOrdenId(id);
            BigDecimal totalProductos = BigDecimal.ZERO;

            if (!request.getProductos().isEmpty()) {
                List<Long> productoIds = request.getProductos().stream()
                        .map(OrdenTrabajoRequest.ProductoItem::getProductoId)
                        .toList();
                Map<Long, Producto> productosMap = productoRepository.findAllById(productoIds).stream()
                        .collect(Collectors.toMap(Producto::getId, p -> p));

                for (OrdenTrabajoRequest.ProductoItem item : request.getProductos()) {
                    Producto producto = productosMap.get(item.getProductoId());
                    if (producto == null) {
                        throw new ResourceNotFoundException("Producto", item.getProductoId());
                    }
                    int cantidad = item.getCantidad() != null ? item.getCantidad() : 1;
                    validarStockDisponible(producto.getId(), cantidad);
                    OrdenProducto op = new OrdenProducto();
                    op.setOrden(orden);
                    op.setProducto(producto);
                    op.setCantidad(cantidad);
                    op.setPrecioUnitario(producto.getPrecioVenta());
                    op.setSubtotal(producto.getPrecioVenta().multiply(BigDecimal.valueOf(cantidad)));
                    ordenProductoRepository.save(op);
                    totalProductos = totalProductos.add(op.getSubtotal());
                }
            }
            orden.setTotalProductos(totalProductos);
        }

        actualizarTotales(orden, orden.getTotalServicios(), orden.getTotalProductos());

        return buildFullResponse(orden);
    }

    @Transactional
    public OrdenTrabajoResponse agregarObservacion(Long id, ObservacionRequest request) {
        OrdenTrabajo orden = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de trabajo", id));

        OrdenObservacion obs = new OrdenObservacion();
        obs.setOrden(orden);
        obs.setUsuario(obtenerUsuarioActual());
        obs.setComentario(request.getComentario());
        observacionRepository.save(obs);

        return buildFullResponse(orden);
    }

    public void delete(Long id, String motivo) {
        OrdenTrabajo orden = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de trabajo", id));
        ordenValidator.validarTerminal(orden.getEstado());
        orden.marcarComoEliminado(obtenerUsuarioActual(), motivo);
        ordenRepository.save(orden);
    }

    private String obtenerUsuarioActual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SISTEMA";
    }

    private void registrarHistorial(OrdenTrabajo orden, String anterior, String nuevo, String observacion) {
        HistorialEstado h = new HistorialEstado();
        h.setOrden(orden);
        h.setEstadoAnterior(anterior);
        h.setEstadoNuevo(nuevo);
        h.setUsuario(obtenerUsuarioActual());
        h.setObservacion(observacion);
        historialRepository.save(h);
    }

    private void actualizarTotales(OrdenTrabajo orden, BigDecimal totalServicios, BigDecimal totalProductos) {
        orden.setTotalServicios(totalServicios);
        orden.setTotalProductos(totalProductos);
        orden.setTotalGeneral(totalServicios.add(totalProductos));
        ordenRepository.save(orden);
    }

    private OrdenTrabajoResponse buildFullResponse(OrdenTrabajo orden) {
        List<OrdenServicio> servicios = ordenServicioRepository.findByOrdenId(orden.getId());
        List<OrdenProducto> productos = ordenProductoRepository.findByOrdenId(orden.getId());
        List<HistorialEstado> historial = historialRepository.findByOrdenIdOrderByFechaAsc(orden.getId());
        List<OrdenObservacion> observaciones = observacionRepository.findByOrdenIdOrderByFechaAsc(orden.getId());
        return ordenMapper.toResponse(orden, servicios, productos, historial, observaciones);
    }

    private String generarNumeroOrden() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void validarStockDisponible(Long productoId, int cantidadSolicitada) {
        inventarioRepository.findByProductoId(productoId).ifPresent(inv -> {
            if (inv.getCantidadActual() < cantidadSolicitada) {
                throw new BadRequestException(
                    "Stock insuficiente para el producto " + inv.getProducto().getNombre() +
                    ". Disponible: " + inv.getCantidadActual() + ", solicitado: " + cantidadSolicitada);
            }
        });
    }

    private void descontarInventario(OrdenTrabajo orden) {
        List<OrdenProducto> productos = ordenProductoRepository.findByOrdenId(orden.getId());
        List<Long> productoIds = productos.stream()
                .map(op -> op.getProducto().getId())
                .toList();
        Map<Long, Inventario> inventarioMap = inventarioRepository.findByProductoIdIn(productoIds).stream()
                .collect(Collectors.toMap(i -> i.getProducto().getId(), i -> i));

        for (OrdenProducto op : productos) {
            Inventario inv = inventarioMap.get(op.getProducto().getId());
            if (inv != null) {
                int nuevaCantidad = inv.getCantidadActual() - op.getCantidad();
                inv.setCantidadActual(Math.max(nuevaCantidad, 0));
                inventarioRepository.save(inv);
            }

            MovimientoInventario movimiento = new MovimientoInventario();
            movimiento.setProducto(op.getProducto());
            movimiento.setTipoMovimiento("SALIDA");
            movimiento.setCantidad(op.getCantidad());
            movimiento.setMotivo("Orden #" + orden.getNumeroOrden());
            movimiento.setUsuario(obtenerUsuarioActual());
            movimientoRepository.save(movimiento);
        }
    }

    private PagedResponse<OrdenTrabajoResponse> toPagedResponse(Page<OrdenTrabajo> page) {
        List<OrdenTrabajo> ordenes = page.getContent();
        if (ordenes.isEmpty()) {
            return new PagedResponse<>(List.of(), page.getNumber(), page.getSize(),
                    page.getTotalElements(), page.getTotalPages(), page.isLast());
        }

        List<Long> ids = ordenes.stream().map(OrdenTrabajo::getId).toList();
        Map<Long, List<OrdenServicio>> serviciosPorOrden = ordenServicioRepository.findByOrdenIdIn(ids).stream()
                .collect(Collectors.groupingBy(os -> os.getOrden().getId()));
        Map<Long, List<OrdenProducto>> productosPorOrden = ordenProductoRepository.findByOrdenIdIn(ids).stream()
                .collect(Collectors.groupingBy(op -> op.getOrden().getId()));
        Map<Long, List<HistorialEstado>> historialPorOrden = historialRepository.findByOrdenIdIn(ids).stream()
                .collect(Collectors.groupingBy(h -> h.getOrden().getId()));
        Map<Long, List<OrdenObservacion>> observacionesPorOrden = observacionRepository.findByOrdenIdIn(ids).stream()
                .collect(Collectors.groupingBy(o -> o.getOrden().getId()));

        List<OrdenTrabajoResponse> content = ordenes.stream()
                .map(orden -> ordenMapper.toResponse(
                        orden,
                        serviciosPorOrden.getOrDefault(orden.getId(), List.of()),
                        productosPorOrden.getOrDefault(orden.getId(), List.of()),
                        historialPorOrden.getOrDefault(orden.getId(), List.of()),
                        observacionesPorOrden.getOrDefault(orden.getId(), List.of())))
                .toList();

        return new PagedResponse<>(
                content, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isLast()
        );
    }
}