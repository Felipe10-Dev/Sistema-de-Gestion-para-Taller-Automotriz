package com.serviteca.caja.service;

import com.serviteca.caja.dto.PagoRequest;
import com.serviteca.caja.dto.PagoResponse;
import com.serviteca.caja.entity.*;
import com.serviteca.caja.enums.EstadoFinanciero;
import com.serviteca.caja.repository.*;
import com.serviteca.orden.entity.OrdenTrabajo;
import com.serviteca.orden.repository.OrdenTrabajoRepository;
import com.serviteca.shared.exception.BadRequestException;
import com.serviteca.shared.exception.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final OrdenTrabajoRepository ordenRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final CajaRepository cajaRepository;
    private final CajaService cajaService;

    public PagoService(PagoRepository pagoRepository, OrdenTrabajoRepository ordenRepository,
                       MetodoPagoRepository metodoPagoRepository, CajaRepository cajaRepository,
                       CajaService cajaService) {
        this.pagoRepository = pagoRepository;
        this.ordenRepository = ordenRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.cajaRepository = cajaRepository;
        this.cajaService = cajaService;
    }

    public List<PagoResponse> listarPagos() {
        return pagoRepository.findAllByOrderByFechaDesc().stream()
                .map(this::toResponse).toList();
    }

    public List<PagoResponse> listarPagosPorOrden(Long ordenId) {
        return pagoRepository.findByOrdenIdOrderByFechaAsc(ordenId).stream()
                .map(this::toResponse).toList();
    }

    @Transactional
    public PagoResponse registrarPago(PagoRequest request) {
        OrdenTrabajo orden = ordenRepository.findById(request.getOrdenId())
                .orElseThrow(() -> new ResourceNotFoundException("Orden de trabajo", request.getOrdenId()));

        if ("CANCELADO".equals(orden.getEstado())) {
            throw new BadRequestException("No se pueden registrar pagos en \u00f3rdenes canceladas");
        }

        MetodoPago metodo = metodoPagoRepository.findById(request.getMetodoPagoId())
                .orElseThrow(() -> new ResourceNotFoundException("M\u00e9todo de pago", request.getMetodoPagoId()));

        if (!metodo.isActivo()) {
            throw new BadRequestException("El m\u00e9todo de pago '" + metodo.getNombre() + "' no est\u00e1 activo");
        }

        if (request.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El valor del pago debe ser mayor a cero");
        }

        BigDecimal totalPagado = pagoRepository.sumByOrdenIdAndAnuladoFalse(orden.getId());
        BigDecimal saldoPendiente = orden.getTotalGeneral().subtract(totalPagado);

        if (request.getValor().compareTo(saldoPendiente) > 0) {
            throw new BadRequestException("El pago de $" + request.getValor() +
                    " excede el saldo pendiente de $" + saldoPendiente);
        }

        String usuario = obtenerUsuarioActual();
        Pago pago = new Pago();
        pago.setOrden(orden);
        pago.setUsuario(usuario);
        pago.setMetodoPago(metodo);
        pago.setValor(request.getValor());
        pago.setObservacion(request.getObservacion());
        pago = pagoRepository.save(pago);

        actualizarEstadoFinanciero(orden);

        registrarMovimientoEnCaja(orden, pago, usuario);

        return toResponse(pago);
    }

    @Transactional
    public PagoResponse anularPago(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", id));

        if (pago.isPagoAnulado()) {
            throw new BadRequestException("El pago ya fue anulado");
        }

        pago.setPagoAnulado(true);
        Pago pagoGuardado = pagoRepository.save(pago);

        OrdenTrabajo orden = pagoGuardado.getOrden();
        actualizarEstadoFinanciero(orden);

        String usuario = obtenerUsuarioActual();
        String descripcion = "Anulaci\u00f3n del pago #" + pagoGuardado.getId() + " por $" + pagoGuardado.getValor();
        Long ordenId = orden.getId();
        Long metodoPagoId = pagoGuardado.getMetodoPago().getId();
        BigDecimal valorNegado = pagoGuardado.getValor().negate();
        cajaRepository.findByUsuarioAndEstado(usuario, "ABIERTA").ifPresent(caja -> {
            cajaService.registrarMovimientoPago(caja, "ANULACION", descripcion,
                    valorNegado, ordenId, metodoPagoId);
        });

        return toResponse(pagoGuardado);
    }

    public BigDecimal consultarSaldoPendiente(Long ordenId) {
        OrdenTrabajo orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de trabajo", ordenId));
        BigDecimal totalPagado = pagoRepository.sumByOrdenIdAndAnuladoFalse(ordenId);
        return orden.getTotalGeneral().subtract(totalPagado);
    }

    private void actualizarEstadoFinanciero(OrdenTrabajo orden) {
        BigDecimal totalPagado = pagoRepository.sumByOrdenIdAndAnuladoFalse(orden.getId());
        String estado;
        if (totalPagado.compareTo(BigDecimal.ZERO) == 0) {
            estado = EstadoFinanciero.SIN_PAGAR.name();
        } else if (totalPagado.compareTo(orden.getTotalGeneral()) >= 0) {
            estado = EstadoFinanciero.PAGADA.name();
        } else {
            estado = EstadoFinanciero.PARCIAL.name();
        }
        orden.setEstadoFinanciero(estado);
        ordenRepository.save(orden);
    }

    private void registrarMovimientoEnCaja(OrdenTrabajo orden, Pago pago, String usuario) {
        cajaRepository.findByUsuarioAndEstado(usuario, "ABIERTA").ifPresent(caja -> {
            String descripcion = "Pago orden #" + orden.getNumeroOrden() + " - $" + pago.getValor();
            cajaService.registrarMovimientoPago(caja, "INGRESO_PAGO", descripcion,
                    pago.getValor(), orden.getId(), pago.getMetodoPago().getId());
        });
    }

    private String obtenerUsuarioActual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SISTEMA";
    }

    private PagoResponse toResponse(Pago pago) {
        PagoResponse r = new PagoResponse();
        r.setId(pago.getId());
        r.setOrdenId(pago.getOrden().getId());
        r.setOrdenNumero(pago.getOrden().getNumeroOrden());
        r.setUsuario(pago.getUsuario());
        r.setFecha(pago.getFecha());
        r.setMetodoPagoId(pago.getMetodoPago().getId());
        r.setMetodoPagoNombre(pago.getMetodoPago().getNombre());
        r.setValor(pago.getValor());
        r.setObservacion(pago.getObservacion());
        r.setPagoAnulado(pago.isPagoAnulado());
        r.setPagoOriginalId(pago.getPagoOriginal() != null ? pago.getPagoOriginal().getId() : null);
        return r;
    }
}
