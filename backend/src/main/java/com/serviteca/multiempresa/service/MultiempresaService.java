package com.serviteca.multiempresa.service;

import com.serviteca.multiempresa.dto.*;
import com.serviteca.multiempresa.entity.*;
import com.serviteca.multiempresa.repository.*;
import com.serviteca.cliente.repository.ClienteRepository;
import com.serviteca.orden.repository.OrdenTrabajoRepository;
import com.serviteca.producto.entity.Producto;
import com.serviteca.producto.repository.ProductoRepository;
import com.serviteca.proveedor.repository.ProveedorRepository;
import com.serviteca.shared.util.TenantContext;
import com.serviteca.vehiculo.repository.VehiculoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MultiempresaService {

    private final EmpresaRepository empresaRepository;
    private final SedeRepository sedeRepository;
    private final TransferenciaInventarioRepository transferenciaRepository;
    private final TransferenciaProductoRepository transferenciaProductoRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final ProveedorRepository proveedorRepository;

    public MultiempresaService(EmpresaRepository empresaRepository,
                               SedeRepository sedeRepository,
                               TransferenciaInventarioRepository transferenciaRepository,
                               TransferenciaProductoRepository transferenciaProductoRepository,
                               ProductoRepository productoRepository,
                               ClienteRepository clienteRepository,
                               VehiculoRepository vehiculoRepository,
                               OrdenTrabajoRepository ordenTrabajoRepository,
                               ProveedorRepository proveedorRepository) {
        this.empresaRepository = empresaRepository;
        this.sedeRepository = sedeRepository;
        this.transferenciaRepository = transferenciaRepository;
        this.transferenciaProductoRepository = transferenciaProductoRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.ordenTrabajoRepository = ordenTrabajoRepository;
        this.proveedorRepository = proveedorRepository;
    }

    // --- Empresa CRUD ---

    public List<EmpresaResponse> listarEmpresas() {
        return empresaRepository.findAll().stream()
                .map(this::toEmpresaResponse)
                .collect(Collectors.toList());
    }

    public EmpresaResponse obtenerEmpresa(Long id) {
        return toEmpresaResponse(empresaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada")));
    }

    @Transactional
    public EmpresaResponse crearEmpresa(EmpresaRequest request) {
        Empresa e = new Empresa();
        e.setNombre(request.getNombre());
        e.setRazonSocial(request.getRazonSocial());
        e.setNit(request.getNit());
        e.setDireccion(request.getDireccion());
        e.setCiudad(request.getCiudad());
        e.setTelefono(request.getTelefono());
        e.setEmail(request.getEmail());
        return toEmpresaResponse(empresaRepository.save(e));
    }

    @Transactional
    public EmpresaResponse actualizarEmpresa(Long id, EmpresaRequest request) {
        Empresa e = empresaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada"));
        if (request.getNombre() != null) e.setNombre(request.getNombre());
        if (request.getRazonSocial() != null) e.setRazonSocial(request.getRazonSocial());
        if (request.getNit() != null) e.setNit(request.getNit());
        if (request.getDireccion() != null) e.setDireccion(request.getDireccion());
        if (request.getCiudad() != null) e.setCiudad(request.getCiudad());
        if (request.getTelefono() != null) e.setTelefono(request.getTelefono());
        if (request.getEmail() != null) e.setEmail(request.getEmail());
        return toEmpresaResponse(empresaRepository.save(e));
    }

    @Transactional
    public void eliminarEmpresa(Long id, String motivo) {
        Empresa e = empresaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada"));
        e.setActivo(false);
        e.setFechaEliminacion(LocalDateTime.now());
        e.setEliminadoPor(obtenerUsuarioActual());
        e.setMotivoEliminacion(motivo);
        empresaRepository.save(e);
    }

    // --- Sede CRUD ---

    public List<SedeResponse> listarSedesPorEmpresa(Long empresaId) {
        return sedeRepository.findByEmpresaId(empresaId).stream()
                .map(this::toSedeResponse)
                .collect(Collectors.toList());
    }

    public List<SedeResponse> listarSedesActivas(Long empresaId) {
        return sedeRepository.findByEmpresaIdAndActivoTrue(empresaId).stream()
                .map(this::toSedeResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SedeResponse crearSede(Long empresaId, SedeRequest request) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada"));
        Sede s = new Sede();
        s.setEmpresa(empresa);
        s.setNombre(request.getNombre());
        s.setDireccion(request.getDireccion());
        s.setCiudad(request.getCiudad());
        s.setTelefono(request.getTelefono());
        if (request.getActivo() != null) s.setActivo(request.getActivo());
        return toSedeResponse(sedeRepository.save(s));
    }

    @Transactional
    public SedeResponse actualizarSede(Long id, SedeRequest request) {
        Sede s = sedeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sede no encontrada"));
        if (request.getNombre() != null) s.setNombre(request.getNombre());
        if (request.getDireccion() != null) s.setDireccion(request.getDireccion());
        if (request.getCiudad() != null) s.setCiudad(request.getCiudad());
        if (request.getTelefono() != null) s.setTelefono(request.getTelefono());
        if (request.getActivo() != null) s.setActivo(request.getActivo());
        return toSedeResponse(sedeRepository.save(s));
    }

    @Transactional
    public void eliminarSede(Long id, String motivo) {
        Sede s = sedeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sede no encontrada"));
        s.setActivo(false);
        s.setFechaEliminacion(LocalDateTime.now());
        s.setEliminadoPor(obtenerUsuarioActual());
        s.setMotivoEliminacion(motivo);
        sedeRepository.save(s);
    }

    // --- Transferencias ---

    @Transactional
    public TransferenciaResponse crearTransferencia(TransferenciaRequest request) {
        Long empresaId = TenantContext.getEmpresaId();
        if (empresaId == null) throw new IllegalStateException("No hay empresa activa en el contexto");

        TransferenciaInventario t = new TransferenciaInventario();
        t.setEmpresaId(empresaId);
        t.setSedeOrigenId(request.getSedeOrigenId());
        t.setSedeDestinoId(request.getSedeDestinoId());

        var auth = SecurityContextHolder.getContext().getAuthentication();
        t.setUsuario(auth != null ? auth.getName() : "system");
        t.setObservaciones(request.getObservaciones());

        List<TransferenciaProducto> productos = new ArrayList<>();
        if (request.getProductos() != null) {
            for (TransferenciaRequest.TransferenciaProductoItem item : request.getProductos()) {
                TransferenciaProducto tp = new TransferenciaProducto();
                tp.setTransferencia(t);
                tp.setProductoId(item.getProductoId());
                tp.setCantidad(item.getCantidad());
                productos.add(tp);
            }
        }
        t.setProductos(productos);

        return toTransferenciaResponse(transferenciaRepository.save(t));
    }

    public List<TransferenciaResponse> listarTransferencias() {
        Long empresaId = TenantContext.getEmpresaId();
        if (empresaId == null) return List.of();
        List<TransferenciaInventario> transferencias = transferenciaRepository.findByEmpresaIdOrderByFechaDesc(empresaId);
        if (transferencias.isEmpty()) return List.of();

        Set<Long> sedeIds = new HashSet<>();
        Set<Long> productoIds = new HashSet<>();
        for (TransferenciaInventario t : transferencias) {
            sedeIds.add(t.getSedeOrigenId());
            sedeIds.add(t.getSedeDestinoId());
            if (t.getProductos() != null) {
                t.getProductos().forEach(tp -> productoIds.add(tp.getProductoId()));
            }
        }

        Map<Long, String> sedeNombres = sedeRepository.findAllById(sedeIds).stream()
                .collect(Collectors.toMap(Sede::getId, Sede::getNombre));
        Map<Long, Producto> productosMap = productoRepository.findAllById(productoIds).stream()
                .collect(Collectors.toMap(Producto::getId, p -> p));

        return transferencias.stream()
                .map(t -> toTransferenciaResponse(t, sedeNombres, productosMap))
                .collect(Collectors.toList());
    }

    private TransferenciaResponse toTransferenciaResponse(TransferenciaInventario t,
                                                          Map<Long, String> sedeNombres,
                                                          Map<Long, Producto> productosMap) {
        TransferenciaResponse r = new TransferenciaResponse();
        r.setId(t.getId());
        r.setEmpresaId(t.getEmpresaId());
        r.setSedeOrigenId(t.getSedeOrigenId());
        r.setSedeDestinoId(t.getSedeDestinoId());
        r.setFecha(t.getFecha());
        r.setUsuario(t.getUsuario());
        r.setObservaciones(t.getObservaciones());

        r.setSedeOrigenNombre(sedeNombres.get(t.getSedeOrigenId()));
        r.setSedeDestinoNombre(sedeNombres.get(t.getSedeDestinoId()));

        if (t.getProductos() != null) {
            r.setProductos(t.getProductos().stream().map(tp -> {
                TransferenciaResponse.ProductoTransferido pt = new TransferenciaResponse.ProductoTransferido();
                pt.setProductoId(tp.getProductoId());
                pt.setCantidad(tp.getCantidad());
                Producto p = productosMap.get(tp.getProductoId());
                if (p != null) {
                    pt.setProductoNombre(p.getNombre());
                    pt.setProductoCodigo(p.getCodigo());
                }
                return pt;
            }).collect(Collectors.toList()));
        }
        return r;
    }

    // --- Dashboard Consolidado ---

    public DashboardConsolidadoResponse dashboardConsolidado(Long empresaId) {
        DashboardConsolidadoResponse r = new DashboardConsolidadoResponse();
        r.setTotalClientes(countClientes(empresaId));
        r.setTotalVehiculos(countVehiculos(empresaId));
        r.setOrdenesActivas(countOrdenesActivas(empresaId));
        r.setOrdenesPendientesPago(countOrdenesPendientesPago(empresaId));
        r.setTotalIngresos(sumIngresos(empresaId));
        r.setTotalProductos(countProductos(empresaId));
        r.setTotalProveedores(countProveedores(empresaId));
        return r;
    }

    // --- Queries for consolidated dashboard ---

    private long countClientes(Long empresaId) {
        return clienteRepository.countByEmpresaIdAndActivoTrue(empresaId);
    }

    private long countVehiculos(Long empresaId) {
        return vehiculoRepository.countByEmpresaIdAndActivoTrue(empresaId);
    }

    private long countOrdenesActivas(Long empresaId) {
        return ordenTrabajoRepository.countByEmpresaIdAndEstado(empresaId, "PENDIENTE")
             + ordenTrabajoRepository.countByEmpresaIdAndEstado(empresaId, "EN_DIAGNOSTICO")
             + ordenTrabajoRepository.countByEmpresaIdAndEstado(empresaId, "EN_PROCESO")
             + ordenTrabajoRepository.countByEmpresaIdAndEstado(empresaId, "ESPERANDO_REPUESTOS")
             + ordenTrabajoRepository.countByEmpresaIdAndEstado(empresaId, "LISTO_PARA_ENTREGA");
    }

    private long countOrdenesPendientesPago(Long empresaId) {
        return ordenTrabajoRepository.countByEmpresaIdAndEstadoFinanciero(empresaId, "SIN_PAGAR")
             + ordenTrabajoRepository.countByEmpresaIdAndEstadoFinanciero(empresaId, "PARCIAL");
    }

    private BigDecimal sumIngresos(Long empresaId) {
        return ordenTrabajoRepository.sumTotalGeneralByEmpresaId(empresaId);
    }

    private long countProductos(Long empresaId) {
        return productoRepository.countByEmpresaIdAndActivoTrue(empresaId);
    }

    private long countProveedores(Long empresaId) {
        return proveedorRepository.countByEmpresaIdAndActivoTrue(empresaId);
    }

    private String obtenerUsuarioActual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SISTEMA";
    }

    // --- Mappers ---

    private EmpresaResponse toEmpresaResponse(Empresa e) {
        EmpresaResponse r = new EmpresaResponse();
        r.setId(e.getId());
        r.setNombre(e.getNombre());
        r.setRazonSocial(e.getRazonSocial());
        r.setNit(e.getNit());
        r.setDireccion(e.getDireccion());
        r.setCiudad(e.getCiudad());
        r.setTelefono(e.getTelefono());
        r.setEmail(e.getEmail());
        r.setActivo(e.isActivo());
        r.setFechaCreacion(e.getFechaCreacion());
        return r;
    }

    private SedeResponse toSedeResponse(Sede s) {
        SedeResponse r = new SedeResponse();
        r.setId(s.getId());
        r.setEmpresaId(s.getEmpresa().getId());
        r.setEmpresaNombre(s.getEmpresa().getNombre());
        r.setNombre(s.getNombre());
        r.setDireccion(s.getDireccion());
        r.setCiudad(s.getCiudad());
        r.setTelefono(s.getTelefono());
        r.setActivo(s.isActivo());
        return r;
    }

    private TransferenciaResponse toTransferenciaResponse(TransferenciaInventario t) {
        TransferenciaResponse r = new TransferenciaResponse();
        r.setId(t.getId());
        r.setEmpresaId(t.getEmpresaId());
        r.setSedeOrigenId(t.getSedeOrigenId());
        r.setSedeDestinoId(t.getSedeDestinoId());
        r.setFecha(t.getFecha());
        r.setUsuario(t.getUsuario());
        r.setObservaciones(t.getObservaciones());

        try {
            sedeRepository.findById(t.getSedeOrigenId())
                    .ifPresent(s -> r.setSedeOrigenNombre(s.getNombre()));
            sedeRepository.findById(t.getSedeDestinoId())
                    .ifPresent(s -> r.setSedeDestinoNombre(s.getNombre()));
        } catch (Exception ignored) {}

        if (t.getProductos() != null) {
            r.setProductos(t.getProductos().stream().map(tp -> {
                TransferenciaResponse.ProductoTransferido pt = new TransferenciaResponse.ProductoTransferido();
                pt.setProductoId(tp.getProductoId());
                pt.setCantidad(tp.getCantidad());
                try {
                    productoRepository.findById(tp.getProductoId()).ifPresent(p -> {
                        pt.setProductoNombre(p.getNombre());
                        pt.setProductoCodigo(p.getCodigo());
                    });
                } catch (Exception ignored) {}
                return pt;
            }).collect(Collectors.toList()));
        }
        return r;
    }

}
