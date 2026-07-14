package com.serviteca.dashboard.service;

import com.serviteca.caja.repository.CajaRepository;
import com.serviteca.cliente.repository.ClienteRepository;
import com.serviteca.compra.repository.OrdenCompraRepository;
import com.serviteca.dashboard.dto.DashboardResponse;
import com.serviteca.inventario.repository.InventarioRepository;
import com.serviteca.orden.repository.OrdenTrabajoRepository;
import com.serviteca.usuario.repository.UsuarioRepository;
import com.serviteca.vehiculo.mantenimiento.repository.MantenimientoRecomendacionRepository;
import com.serviteca.vehiculo.repository.VehiculoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class DashboardService {

    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    private final OrdenTrabajoRepository ordenRepository;
    private final InventarioRepository inventarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final CajaRepository cajaRepository;
    private final MantenimientoRecomendacionRepository mantenimientoRepository;
    private final OrdenCompraRepository ordenCompraRepository;

    public DashboardService(ClienteRepository clienteRepository, VehiculoRepository vehiculoRepository,
                            OrdenTrabajoRepository ordenRepository, InventarioRepository inventarioRepository,
                            UsuarioRepository usuarioRepository, CajaRepository cajaRepository,
                            MantenimientoRecomendacionRepository mantenimientoRepository,
                            OrdenCompraRepository ordenCompraRepository) {
        this.clienteRepository = clienteRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.ordenRepository = ordenRepository;
        this.inventarioRepository = inventarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.cajaRepository = cajaRepository;
        this.mantenimientoRepository = mantenimientoRepository;
        this.ordenCompraRepository = ordenCompraRepository;
    }

    public DashboardResponse getDashboard() {
        DashboardResponse response = new DashboardResponse();
        response.setTotalClientes(clienteRepository.count());
        response.setTotalVehiculos(vehiculoRepository.count());
        response.setOrdenesPendientes(ordenRepository.countByEstado("PENDIENTE"));
        response.setOrdenesEnDiagnostico(ordenRepository.countByEstado("EN_DIAGNOSTICO"));
        response.setOrdenesEnProceso(ordenRepository.countByEstado("EN_PROCESO"));
        response.setOrdenesEsperandoRepuestos(ordenRepository.countByEstado("ESPERANDO_REPUESTOS"));
        response.setOrdenesListas(ordenRepository.countByEstado("LISTO_PARA_ENTREGA"));

        LocalDate hoy = LocalDate.now();
        response.setOrdenesEntregadasHoy(
                ordenRepository.countByEstadoAndFechaSalidaBetween("ENTREGADO",
                        hoy.atStartOfDay(), hoy.atTime(LocalTime.MAX)));
        response.setIngresosHoy(
                ordenRepository.sumTotalGeneralByEstadoAndFechaSalidaBetween("ENTREGADO",
                        hoy.atStartOfDay(), hoy.atTime(LocalTime.MAX)));

        response.setProductosBajoStock(inventarioRepository.findProductosBajoStock().size());
        response.setTotalUsuarios(usuarioRepository.count());

        String usuario = obtenerUsuarioActual();
        response.setCajaAbierta(cajaRepository.findByUsuarioAndEstado(usuario, "ABIERTA").isPresent());
        response.setCajaUsuario(usuario);

        response.setOrdenesSinPagar(ordenRepository.countByEstadoFinanciero("SIN_PAGAR"));
        response.setOrdenesParciales(ordenRepository.countByEstadoFinanciero("PARCIAL"));
        response.setOrdenesPagadas(ordenRepository.countByEstadoFinanciero("PAGADA"));
        response.setSaldoPendienteTotal(ordenRepository.sumSaldoPendienteTotal());

        response.setVehiculosProximosMantenimiento(
                mantenimientoRepository.findAllActivos().stream()
                        .map(r -> r.getVehiculo().getId())
                        .distinct()
                        .count());

        LocalDateTime hace12Meses = LocalDateTime.now().minusMonths(12);
        response.setVehiculosSinVisitas12Meses(
                ordenRepository.findVehiculosSinVisitasDesde(hace12Meses).size());

        response.setClientesFrecuentes(
                ordenRepository.findClientesFrecuentes(PageRequest.of(0, 5)).size());

        response.setVehiculosMayorInversion(
                ordenRepository.findVehiculosMayorInversion(PageRequest.of(0, 5)).size());

        response.setProductosBajoMinimo(inventarioRepository.findProductosBajoStock().size());
        response.setProductosAgotados(inventarioRepository.countByCantidadActual(0));
        response.setValorTotalInventario(inventarioRepository.sumValorTotalInventario());

        LocalDate inicioMes = hoy.withDayOfMonth(1);
        response.setComprasDelMes(ordenCompraRepository.countByEstadoAndFechaBetween(
                "RECIBIDA", inicioMes.atStartOfDay(), hoy.atTime(LocalTime.MAX)));

        response.setProveedoresMasUtilizados(
                ordenCompraRepository.findProveedoresMasUtilizados().size());

        return response;
    }

    private String obtenerUsuarioActual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SISTEMA";
    }
}
