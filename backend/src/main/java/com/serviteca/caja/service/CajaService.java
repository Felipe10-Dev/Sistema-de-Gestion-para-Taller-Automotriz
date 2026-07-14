package com.serviteca.caja.service;

import com.serviteca.caja.dto.AperturaCajaRequest;
import com.serviteca.caja.dto.CajaResponse;
import com.serviteca.caja.dto.CierreCajaRequest;
import com.serviteca.caja.dto.MovimientoCajaResponse;
import com.serviteca.caja.entity.Caja;
import com.serviteca.caja.entity.MovimientoCaja;
import com.serviteca.caja.repository.CajaRepository;
import com.serviteca.caja.repository.MovimientoCajaRepository;
import com.serviteca.caja.repository.PagoRepository;
import com.serviteca.shared.exception.BadRequestException;
import com.serviteca.shared.exception.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class CajaService {

    private final CajaRepository cajaRepository;
    private final PagoRepository pagoRepository;
    private final MovimientoCajaRepository movimientoRepository;

    public CajaService(CajaRepository cajaRepository, PagoRepository pagoRepository,
                       MovimientoCajaRepository movimientoRepository) {
        this.cajaRepository = cajaRepository;
        this.pagoRepository = pagoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    public CajaResponse obtenerCajaActual() {
        String usuario = obtenerUsuarioActual();
        return cajaRepository.findByUsuarioAndEstado(usuario, "ABIERTA")
                .map(this::toResponse)
                .orElse(null);
    }

    public List<CajaResponse> listarHistorial() {
        return cajaRepository.findAllByOrderByFechaAperturaDesc().stream()
                .map(this::toResponse).toList();
    }

    @Transactional
    public CajaResponse abrirCaja(AperturaCajaRequest request) {
        String usuario = obtenerUsuarioActual();

        if (cajaRepository.findByUsuarioAndEstado(usuario, "ABIERTA").isPresent()) {
            throw new BadRequestException("Ya tienes una caja abierta. Ci\u00e9rrala antes de abrir una nueva.");
        }

        Caja caja = new Caja();
        caja.setUsuario(usuario);
        caja.setMontoInicial(request.getMontoInicial() != null ? request.getMontoInicial() : BigDecimal.ZERO);
        caja.setObservaciones(request.getObservacion());
        caja.setEstado("ABIERTA");
        caja = cajaRepository.save(caja);

        registrarMovimiento(caja, "APERTURA", "Apertura de caja", caja.getMontoInicial(), null, null);

        return toResponse(caja);
    }

    @Transactional
    public CajaResponse cerrarCaja(Long id, CierreCajaRequest request) {
        Caja caja = cajaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Caja", id));

        if (!"ABIERTA".equals(caja.getEstado())) {
            throw new BadRequestException("La caja no est\u00e1 abierta");
        }

        LocalDateTime apertura = caja.getFechaApertura();
        LocalDateTime ahora = LocalDateTime.now();

        BigDecimal totalIngresos = pagoRepository.sumByFechaBetween(apertura, ahora);
        BigDecimal totalEsperado = caja.getMontoInicial().add(totalIngresos);

        caja.setFechaCierre(ahora);
        caja.setTotalIngresos(totalIngresos);
        caja.setTotalEgresos(BigDecimal.ZERO);
        caja.setTotalEsperado(totalEsperado);
        caja.setMontoContado(request.getMontoContado());
        BigDecimal diferencia = request.getMontoContado().subtract(totalEsperado);
        caja.setDiferencia(diferencia);
        if (diferencia.compareTo(BigDecimal.ZERO) < 0 &&
            (request.getObservaciones() == null || request.getObservaciones().isBlank())) {
            throw new BadRequestException("Debe registrar una observaci\u00f3n cuando la diferencia es negativa");
        }
        if (request.getObservaciones() != null && !request.getObservaciones().isBlank()) {
            String obsActual = caja.getObservaciones();
            caja.setObservaciones(obsActual != null ? obsActual + " | " + request.getObservaciones() : request.getObservaciones());
        }
        caja.setEstado("CERRADA");
        caja = cajaRepository.save(caja);

        registrarMovimiento(caja, "CIERRE", "Cierre de caja. Monto contado: $" + request.getMontoContado(),
                totalEsperado, null, null);

        return toResponse(caja);
    }

    public List<MovimientoCajaResponse> listarMovimientos(Long cajaId) {
        List<MovimientoCaja> movimientos;
        if (cajaId != null) {
            movimientos = movimientoRepository.findByCajaIdOrderByFechaAsc(cajaId);
        } else {
            movimientos = movimientoRepository.findAllByOrderByFechaDesc();
        }
        return movimientos.stream().map(this::toMovimientoResponse).toList();
    }

    private void registrarMovimiento(Caja caja, String tipo, String descripcion, BigDecimal monto,
                                      Long ordenId, Long metodoPagoId) {
        MovimientoCaja m = new MovimientoCaja();
        m.setCaja(caja);
        m.setTipo(tipo);
        m.setDescripcion(descripcion);
        m.setMonto(monto);
        m.setUsuario(obtenerUsuarioActual());
        m.setOrdenId(ordenId);
        m.setMetodoPagoId(metodoPagoId);
        movimientoRepository.save(m);
    }

    void registrarMovimientoPago(Caja caja, String tipo, String descripcion, BigDecimal monto,
                                  Long ordenId, Long metodoPagoId) {
        registrarMovimiento(caja, tipo, descripcion, monto, ordenId, metodoPagoId);
    }

    private String obtenerUsuarioActual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SISTEMA";
    }

    private CajaResponse toResponse(Caja caja) {
        CajaResponse r = new CajaResponse();
        r.setId(caja.getId());
        r.setUsuario(caja.getUsuario());
        r.setEstado(caja.getEstado());
        r.setFechaApertura(caja.getFechaApertura());
        r.setFechaCierre(caja.getFechaCierre());
        r.setMontoInicial(caja.getMontoInicial());
        r.setTotalIngresos(caja.getTotalIngresos());
        r.setTotalEgresos(caja.getTotalEgresos());
        r.setTotalEsperado(caja.getTotalEsperado());
        r.setMontoContado(caja.getMontoContado());
        r.setDiferencia(caja.getDiferencia());
        r.setObservaciones(caja.getObservaciones());
        return r;
    }

    private MovimientoCajaResponse toMovimientoResponse(MovimientoCaja m) {
        MovimientoCajaResponse r = new MovimientoCajaResponse();
        r.setId(m.getId());
        r.setTipo(m.getTipo());
        r.setDescripcion(m.getDescripcion());
        r.setMonto(m.getMonto());
        r.setUsuario(m.getUsuario());
        r.setFecha(m.getFecha());
        r.setOrdenId(m.getOrdenId());
        r.setMetodoPagoId(m.getMetodoPagoId());
        return r;
    }
}
