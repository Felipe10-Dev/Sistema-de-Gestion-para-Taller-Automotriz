package com.serviteca.crm.service;

import com.serviteca.caja.repository.PagoRepository;
import com.serviteca.cliente.entity.Cliente;
import com.serviteca.cliente.repository.ClienteRepository;
import com.serviteca.crm.dto.*;
import com.serviteca.crm.entity.ClienteNota;
import com.serviteca.crm.repository.ClienteNotaRepository;
import com.serviteca.orden.entity.OrdenTrabajo;
import com.serviteca.orden.repository.OrdenProductoRepository;
import com.serviteca.orden.repository.OrdenServicioRepository;
import com.serviteca.orden.repository.OrdenTrabajoRepository;
import com.serviteca.shared.exception.ResourceNotFoundException;
import com.serviteca.vehiculo.entity.Vehiculo;
import com.serviteca.vehiculo.mantenimiento.dto.ProximoMantenimientoResponse;
import com.serviteca.vehiculo.mantenimiento.repository.MantenimientoRecomendacionRepository;
import com.serviteca.vehiculo.repository.VehiculoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CrmService {

    public static final int UMBRAL_ORDENES_FRECUENTE = 5;
    public static final BigDecimal UMBRAL_TOTAL_VIP = new BigDecimal("5000000");
    public static final int MESES_INACTIVO = 12;

    private final ClienteRepository clienteRepository;
    private final OrdenTrabajoRepository ordenRepository;
    private final VehiculoRepository vehiculoRepository;
    private final ClienteNotaRepository notaRepository;
    private final PagoRepository pagoRepository;
    private final OrdenServicioRepository ordenServicioRepository;
    private final OrdenProductoRepository ordenProductoRepository;
    private final MantenimientoRecomendacionRepository mantenimientoRepository;

    public CrmService(ClienteRepository clienteRepository, OrdenTrabajoRepository ordenRepository,
                      VehiculoRepository vehiculoRepository, ClienteNotaRepository notaRepository,
                      PagoRepository pagoRepository, OrdenServicioRepository ordenServicioRepository,
                      OrdenProductoRepository ordenProductoRepository,
                      MantenimientoRecomendacionRepository mantenimientoRepository) {
        this.clienteRepository = clienteRepository;
        this.ordenRepository = ordenRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.notaRepository = notaRepository;
        this.pagoRepository = pagoRepository;
        this.ordenServicioRepository = ordenServicioRepository;
        this.ordenProductoRepository = ordenProductoRepository;
        this.mantenimientoRepository = mantenimientoRepository;
    }

    public ClientePerfilResponse obtenerPerfil(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", clienteId));

        long totalOrdenes = ordenRepository.countByClienteId(clienteId);
        BigDecimal totalGastado = ordenRepository.sumTotalGeneralByClienteId(clienteId);
        BigDecimal promedio = totalOrdenes > 0
                ? totalGastado.divide(BigDecimal.valueOf(totalOrdenes), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        List<Vehiculo> vehiculos = vehiculoRepository.findByClienteId(clienteId);

        ClientePerfilResponse perfil = new ClientePerfilResponse();
        perfil.setId(cliente.getId());
        perfil.setTipoDocumento(cliente.getTipoDocumento());
        perfil.setNumeroDocumento(cliente.getNumeroDocumento());
        perfil.setNombre(cliente.getNombre());
        perfil.setApellido(cliente.getApellido());
        perfil.setTelefono(cliente.getTelefono());
        perfil.setEmail(cliente.getEmail());
        perfil.setDireccion(cliente.getDireccion());
        perfil.setActivo(cliente.isActivo());
        perfil.setClasificacion(calcularClasificacion(clienteId));
        perfil.setFechaRegistro(cliente.getFechaCreacion());
        perfil.setPrimeraVisita(ordenRepository.findMinFechaIngresoByClienteId(clienteId).orElse(null));
        perfil.setUltimaVisita(ordenRepository.findMaxFechaIngresoByClienteId(clienteId).orElse(null));
        perfil.setTotalOrdenes(totalOrdenes);
        perfil.setTotalVehiculos(vehiculos.size());
        perfil.setTotalGastado(totalGastado);
        perfil.setPromedioPorOrden(promedio);
        perfil.setNotas(obtenerNotas(clienteId));

        return perfil;
    }

    public String calcularClasificacion(Long clienteId) {
        long totalOrdenes = ordenRepository.countByClienteId(clienteId);
        BigDecimal totalGastado = ordenRepository.sumTotalGeneralByClienteId(clienteId);
        Optional<LocalDateTime> ultimaVisita = ordenRepository.findMaxFechaIngresoByClienteId(clienteId);

        if (ultimaVisita.isEmpty() || ultimaVisita.get().isBefore(LocalDateTime.now().minusMonths(MESES_INACTIVO))) {
            return "INACTIVO";
        }
        if (totalGastado.compareTo(UMBRAL_TOTAL_VIP) >= 0) {
            return "VIP";
        }
        if (totalOrdenes >= UMBRAL_ORDENES_FRECUENTE) {
            return "FRECUENTE";
        }
        return "NUEVO";
    }

    public HistorialEconomicoResponse obtenerHistorialEconomico(Long clienteId) {
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", clienteId));

        long cantidadOrdenes = ordenRepository.countByClienteId(clienteId);
        BigDecimal totalInvertido = ordenRepository.sumTotalGeneralByClienteId(clienteId);
        BigDecimal promedio = cantidadOrdenes > 0
                ? totalInvertido.divide(BigDecimal.valueOf(cantidadOrdenes), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal mayorCompra = ordenRepository.findMaxTotalGeneralByClienteId(clienteId).orElse(BigDecimal.ZERO);
        LocalDateTime ultimaCompra = ordenRepository.findMaxFechaIngresoByClienteId(clienteId).orElse(null);
        BigDecimal totalPendiente = ordenRepository.sumTotalPendienteByClienteId(clienteId);
        BigDecimal totalCancelado = pagoRepository.sumByClienteIdAndAnuladoFalse(clienteId);

        HistorialEconomicoResponse response = new HistorialEconomicoResponse();
        response.setTotalInvertido(totalInvertido);
        response.setCantidadOrdenes(cantidadOrdenes);
        response.setPromedioPorOrden(promedio);
        response.setMayorCompra(mayorCompra);
        response.setUltimaCompra(ultimaCompra);
        response.setTotalPendientePago(totalPendiente);
        response.setTotalCancelado(totalCancelado);
        return response;
    }

    public List<OrdenClienteItem> obtenerHistorialOrdenes(Long clienteId) {
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", clienteId));

        List<OrdenTrabajo> ordenes = ordenRepository.findByClienteIdOrderByFechaIngresoDesc(clienteId);
        if (ordenes.isEmpty()) return List.of();

        List<Long> ids = ordenes.stream().map(OrdenTrabajo::getId).toList();
        Map<Long, List<String>> serviciosPorOrden = ordenServicioRepository.findByOrdenIdIn(ids).stream()
                .collect(Collectors.groupingBy(
                        os -> os.getOrden().getId(),
                        Collectors.mapping(os -> os.getServicio().getNombre() + " x" + os.getCantidad(),
                                Collectors.toList())));
        Map<Long, List<String>> productosPorOrden = ordenProductoRepository.findByOrdenIdIn(ids).stream()
                .collect(Collectors.groupingBy(
                        op -> op.getOrden().getId(),
                        Collectors.mapping(op -> op.getProducto().getNombre() + " x" + op.getCantidad(),
                                Collectors.toList())));

        return ordenes.stream().map(o -> {
            OrdenClienteItem item = new OrdenClienteItem();
            item.setFecha(o.getFechaIngreso());
            if (o.getVehiculo() != null) {
                item.setVehiculoPlaca(o.getVehiculo().getPlaca());
                item.setVehiculoMarca(o.getVehiculo().getMarca());
                item.setVehiculoLinea(o.getVehiculo().getLinea());
            }
            item.setNumeroOrden(o.getNumeroOrden());
            item.setEstado(o.getEstado());
            item.setEstadoFinanciero(o.getEstadoFinanciero());
            item.setTotal(o.getTotalGeneral());
            item.setServicios(serviciosPorOrden.getOrDefault(o.getId(), List.of()));
            item.setProductos(productosPorOrden.getOrDefault(o.getId(), List.of()));
            return item;
        }).toList();
    }

    public List<ClienteNotaResponse> obtenerNotas(Long clienteId) {
        return notaRepository.findByClienteIdOrderByFechaDesc(clienteId).stream()
                .map(this::toNotaResponse)
                .toList();
    }

    @Transactional
    public ClienteNotaResponse agregarNota(Long clienteId, ClienteNotaRequest request) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", clienteId));

        ClienteNota nota = new ClienteNota();
        nota.setCliente(cliente);
        nota.setUsuario(obtenerUsuarioActual());
        nota.setComentario(request.getComentario());
        nota = notaRepository.save(nota);
        return toNotaResponse(nota);
    }

    public List<ClienteInactivoResponse> obtenerClientesInactivos(int meses) {
        LocalDateTime fechaLimite = LocalDateTime.now().minusMonths(meses);
        List<Cliente> clientes = clienteRepository.findClientesSinVisitasDesde(fechaLimite);
        if (clientes.isEmpty()) return List.of();

        List<Long> ids = clientes.stream().map(Cliente::getId).toList();
        Map<Long, Optional<LocalDateTime>> ultimaVisitaPorId = ids.stream()
                .collect(Collectors.toMap(id -> id, ordenRepository::findMaxFechaIngresoByClienteId));
        Map<Long, Long> totalOrdenesPorId = ids.stream()
                .collect(Collectors.toMap(id -> id, ordenRepository::countByClienteId));

        return clientes.stream().map(c -> {
            ClienteInactivoResponse r = new ClienteInactivoResponse();
            r.setId(c.getId());
            r.setNombre(c.getNombre());
            r.setApellido(c.getApellido());
            r.setTelefono(c.getTelefono());
            r.setEmail(c.getEmail());
            Optional<LocalDateTime> ultima = ultimaVisitaPorId.get(c.getId());
            r.setUltimaVisita(ultima.orElse(null));
            r.setMesesSinVisita(ultima.map(u -> ChronoUnit.MONTHS.between(u.toLocalDate(), LocalDate.now())).orElse((long) meses));
            r.setTotalOrdenes(totalOrdenesPorId.getOrDefault(c.getId(), 0L));
            return r;
        }).toList();
    }

    public List<ClienteInactivoResponse> obtenerClientesVip() {
        List<Object[]> topClientes = ordenRepository.findClientesByTotalGastado(PageRequest.of(0, Integer.MAX_VALUE));
        Set<Long> vipIds = topClientes.stream()
                .filter(r -> ((BigDecimal) r[1]).compareTo(UMBRAL_TOTAL_VIP) >= 0)
                .map(r -> (Long) r[0])
                .collect(Collectors.toSet());

        if (vipIds.isEmpty()) return List.of();

        Map<Long, Cliente> clientesMap = clienteRepository.findAllById(vipIds).stream()
                .collect(Collectors.toMap(Cliente::getId, c -> c));

        Map<Long, Optional<LocalDateTime>> ultimaVisitaPorId = vipIds.stream()
                .collect(Collectors.toMap(id -> id, ordenRepository::findMaxFechaIngresoByClienteId));
        Map<Long, Long> totalOrdenesPorId = vipIds.stream()
                .collect(Collectors.toMap(id -> id, ordenRepository::countByClienteId));

        return vipIds.stream().map(id -> {
            Cliente c = clientesMap.get(id);
            if (c == null) return null;
            ClienteInactivoResponse r = new ClienteInactivoResponse();
            r.setId(c.getId());
            r.setNombre(c.getNombre());
            r.setApellido(c.getApellido());
            r.setTelefono(c.getTelefono());
            r.setEmail(c.getEmail());
            r.setUltimaVisita(ultimaVisitaPorId.get(id).orElse(null));
            r.setTotalOrdenes(totalOrdenesPorId.getOrDefault(id, 0L));
            return r;
        }).filter(Objects::nonNull).toList();
    }

    public List<RankingItem> obtenerRanking(String tipo) {
        List<Object[]> resultados;
        String label;

        switch (tipo) {
            case "invertido":
                resultados = ordenRepository.findClientesByTotalGastado(PageRequest.of(0, 10));
                label = "Total invertido";
                break;
            case "visitas":
            case "ordenes":
                resultados = ordenRepository.findClientesFrecuentes(PageRequest.of(0, 10));
                label = "N\u00famero de visitas";
                break;
            case "servicios":
                resultados = ordenServicioRepository.countByClienteGrouped();
                label = "Servicios realizados";
                break;
            case "vehiculos":
                resultados = vehiculoRepository.countVehiculosPorCliente();
                label = "Veh\u00edculos registrados";
                break;
            default:
                return List.of();
        }

        if (resultados.isEmpty()) return List.of();

        Set<Long> ids = resultados.stream()
                .map(r -> (Long) r[0])
                .collect(Collectors.toSet());
        Map<Long, Cliente> clientesMap = clienteRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Cliente::getId, c -> c));

        return resultados.stream().map(r -> {
            Long clienteId = (Long) r[0];
            long valor = r[1] instanceof BigDecimal ? ((BigDecimal) r[1]).longValue() : (Long) r[1];
            Cliente c = clientesMap.get(clienteId);
            RankingItem item = new RankingItem();
            item.setClienteId(clienteId);
            item.setNombreCompleto(c != null ? c.getNombre() + " " + c.getApellido() : "?");
            item.setTipoDocumento(c != null ? c.getTipoDocumento() : "");
            item.setNumeroDocumento(c != null ? c.getNumeroDocumento() : "");
            item.setValor(valor);
            item.setTipo(label);
            return item;
        }).toList();
    }

    public List<ProximoMantenimientoClienteResponse> obtenerProximosMantenimientos(Long clienteId) {
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", clienteId));

        List<Vehiculo> vehiculos = vehiculoRepository.findByClienteId(clienteId);
        List<ProximoMantenimientoClienteResponse> result = new ArrayList<>();

        for (Vehiculo v : vehiculos) {
            List<ProximoMantenimientoResponse> mantenimientos = calcularProximosPorVehiculo(v);
            if (!mantenimientos.isEmpty()) {
                ProximoMantenimientoClienteResponse r = new ProximoMantenimientoClienteResponse();
                r.setVehiculoId(v.getId());
                r.setVehiculoPlaca(v.getPlaca());
                r.setVehiculoMarca(v.getMarca());
                r.setVehiculoLinea(v.getLinea());
                r.setMantenimientos(mantenimientos);
                result.add(r);
            }
        }
        return result;
    }

    private List<ProximoMantenimientoResponse> calcularProximosPorVehiculo(Vehiculo vehiculo) {
        List<com.serviteca.vehiculo.mantenimiento.entity.MantenimientoRecomendacion> recomendaciones =
                mantenimientoRepository.findByVehiculoIdAndActivoTrue(vehiculo.getId());
        if (recomendaciones.isEmpty()) return List.of();

        List<OrdenTrabajo> ordenes = ordenRepository.findByVehiculoIdOrderByFechaIngresoDesc(vehiculo.getId());
        OrdenTrabajo ultimaOrden = ordenes.isEmpty() ? null : ordenes.get(0);
        Integer ultimoKm = ultimaOrden != null ? ultimaOrden.getKilometraje() : 0;
        java.time.LocalDate ultimaFecha = ultimaOrden != null
                ? ultimaOrden.getFechaIngreso().toLocalDate()
                : java.time.LocalDate.now();

        return recomendaciones.stream().map(r -> {
            ProximoMantenimientoResponse p = new ProximoMantenimientoResponse();
            p.setRecomendacionId(r.getId());
            p.setTipo(r.getTipo());
            p.setDescripcion(r.getDescripcion());
            p.setUltimoKilometraje(ultimoKm);

            if (r.getIntervaloKilometraje() != null) {
                p.setProximoKilometraje(ultimoKm + r.getIntervaloKilometraje());
            }
            if (r.getIntervaloDias() != null) {
                p.setProximaFecha(ultimaFecha.plusDays(r.getIntervaloDias()));
            }
            p.setKilometrosRestantes(p.getProximoKilometraje() != null
                    ? Math.max(0, p.getProximoKilometraje() - (ultimoKm != null ? ultimoKm : 0)) : null);
            p.setDiasRestantes(p.getProximaFecha() != null
                    ? Math.max(0, ChronoUnit.DAYS.between(java.time.LocalDate.now(), p.getProximaFecha())) : null);
            p.setAlerta(calcularAlerta(p));
            return p;
        }).toList();
    }

    private String calcularAlerta(ProximoMantenimientoResponse p) {
        boolean kmProximo = p.getKilometrosRestantes() != null && p.getKilometrosRestantes() <= 500;
        boolean diasProximo = p.getDiasRestantes() != null && p.getDiasRestantes() <= 15;
        boolean kmPronto = p.getKilometrosRestantes() != null && p.getKilometrosRestantes() <= 1000;
        boolean diasPronto = p.getDiasRestantes() != null && p.getDiasRestantes() <= 30;

        if (kmProximo || diasProximo) return "PROXIMO";
        if (kmPronto || diasPronto) return "PRONTO";
        if (p.getProximoKilometraje() != null || p.getProximaFecha() != null) return "NORMAL";
        return "SIN_PROGRAMAR";
    }

    public CrmDashboardResponse obtenerDashboardCrm() {
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioMes = hoy.withDayOfMonth(1).atStartOfDay();
        LocalDateTime hace6Meses = LocalDateTime.now().minusMonths(6);
        LocalDateTime hace12Meses = LocalDateTime.now().minusMonths(12);

        CrmDashboardResponse response = new CrmDashboardResponse();
        response.setClientesNuevosEsteMes(clienteRepository.countNuevosEsteMes(inicioMes));
        response.setClientesActivos(ordenRepository.countClientesActivosDesde(hace6Meses));

        List<Object[]> totalGastado = ordenRepository.findClientesByTotalGastado(PageRequest.of(0, Integer.MAX_VALUE));
        long vip = totalGastado.stream()
                .filter(r -> ((BigDecimal) r[1]).compareTo(UMBRAL_TOTAL_VIP) >= 0)
                .count();

        List<Object[]> frecuentesRaw = ordenRepository.findClientesFrecuentes(PageRequest.of(0, Integer.MAX_VALUE));
        long frecuentes = frecuentesRaw.stream()
                .filter(r -> (Long) r[1] >= UMBRAL_ORDENES_FRECUENTE)
                .count();

        response.setClientesFrecuentes(frecuentes);
        response.setClientesVip(vip);
        response.setClientesInactivos12Meses(
                clienteRepository.findClientesSinVisitasDesde(hace12Meses).size());

        response.setClientesConMantenimientoProximo(
                mantenimientoRepository.findAllActivos().stream()
                        .map(r -> r.getVehiculo().getCliente().getId())
                        .distinct()
                        .count());

        response.setTop10ClientesFacturacion(obtenerRanking("invertido"));
        response.setTop10ClientesVisitas(obtenerRanking("visitas"));

        return response;
    }

    private ClienteNotaResponse toNotaResponse(ClienteNota nota) {
        ClienteNotaResponse r = new ClienteNotaResponse();
        r.setId(nota.getId());
        r.setUsuario(nota.getUsuario());
        r.setFecha(nota.getFecha());
        r.setComentario(nota.getComentario());
        return r;
    }

    private String obtenerUsuarioActual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SISTEMA";
    }
}