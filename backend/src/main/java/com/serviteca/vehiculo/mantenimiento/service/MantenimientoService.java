package com.serviteca.vehiculo.mantenimiento.service;

import com.serviteca.orden.entity.OrdenTrabajo;
import com.serviteca.orden.repository.OrdenTrabajoRepository;
import com.serviteca.shared.exception.ResourceNotFoundException;
import com.serviteca.vehiculo.entity.Vehiculo;
import org.springframework.security.core.context.SecurityContextHolder;
import com.serviteca.vehiculo.mantenimiento.dto.MantenimientoRecomendacionRequest;
import com.serviteca.vehiculo.mantenimiento.dto.MantenimientoRecomendacionResponse;
import com.serviteca.vehiculo.mantenimiento.dto.ProximoMantenimientoResponse;
import com.serviteca.vehiculo.mantenimiento.entity.MantenimientoRecomendacion;
import com.serviteca.vehiculo.mantenimiento.repository.MantenimientoRecomendacionRepository;
import com.serviteca.vehiculo.repository.VehiculoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MantenimientoService {

    private final MantenimientoRecomendacionRepository recomendacionRepository;
    private final VehiculoRepository vehiculoRepository;
    private final OrdenTrabajoRepository ordenRepository;

    public MantenimientoService(MantenimientoRecomendacionRepository recomendacionRepository,
                                VehiculoRepository vehiculoRepository,
                                OrdenTrabajoRepository ordenRepository) {
        this.recomendacionRepository = recomendacionRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.ordenRepository = ordenRepository;
    }

    public List<MantenimientoRecomendacionResponse> listarPorVehiculo(Long vehiculoId) {
        return recomendacionRepository.findByVehiculoIdOrderByTipoAsc(vehiculoId).stream()
                .map(this::toResponse)
                .toList();
    }

    public MantenimientoRecomendacionResponse crear(Long vehiculoId, MantenimientoRecomendacionRequest request) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new ResourceNotFoundException("Veh\u00edculo", vehiculoId));

        MantenimientoRecomendacion r = new MantenimientoRecomendacion();
        r.setVehiculo(vehiculo);
        r.setTipo(request.getTipo());
        r.setDescripcion(request.getDescripcion());
        r.setTipoProgramacion(request.getTipoProgramacion());
        r.setIntervaloKilometraje(request.getIntervaloKilometraje());
        r.setIntervaloDias(request.getIntervaloDias());

        r = recomendacionRepository.save(r);
        return toResponse(r);
    }

    public MantenimientoRecomendacionResponse actualizar(Long id, MantenimientoRecomendacionRequest request) {
        MantenimientoRecomendacion r = recomendacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recomendaci\u00f3n de mantenimiento", id));

        r.setTipo(request.getTipo());
        r.setDescripcion(request.getDescripcion());
        r.setTipoProgramacion(request.getTipoProgramacion());
        r.setIntervaloKilometraje(request.getIntervaloKilometraje());
        r.setIntervaloDias(request.getIntervaloDias());

        r = recomendacionRepository.save(r);
        return toResponse(r);
    }

    public void eliminar(Long id, String motivo) {
        MantenimientoRecomendacion r = recomendacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recomendaci\u00f3n de mantenimiento", id));
        r.setActivo(false);
        r.setFechaEliminacion(LocalDateTime.now());
        r.setEliminadoPor(obtenerUsuarioActual());
        r.setMotivoEliminacion(motivo);
        recomendacionRepository.save(r);
    }

    public List<ProximoMantenimientoResponse> calcularProximos(Long vehiculoId) {
        vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new ResourceNotFoundException("Veh\u00edculo", vehiculoId));

        List<MantenimientoRecomendacion> recomendaciones =
                recomendacionRepository.findByVehiculoIdAndActivoTrue(vehiculoId);
        List<ProximoMantenimientoResponse> resultados = new ArrayList<>();

        List<OrdenTrabajo> ordenes = ordenRepository.findByVehiculoIdOrderByFechaIngresoDesc(vehiculoId);
        OrdenTrabajo ultimaOrden = ordenes.isEmpty() ? null : ordenes.get(0);

        int ultimoKilometraje = 0;
        LocalDate ultimaFecha = LocalDate.now();
        if (ultimaOrden != null) {
            ultimoKilometraje = ultimaOrden.getKilometraje() != null ? ultimaOrden.getKilometraje() : 0;
            ultimaFecha = ultimaOrden.getFechaIngreso() != null ?
                    ultimaOrden.getFechaIngreso().toLocalDate() : LocalDate.now();
        }

        for (MantenimientoRecomendacion r : recomendaciones) {
            ProximoMantenimientoResponse p = new ProximoMantenimientoResponse();
            p.setRecomendacionId(r.getId());
            p.setTipo(r.getTipo());
            p.setDescripcion(r.getDescripcion());
            p.setUltimoKilometraje(ultimoKilometraje);
            p.setUltimaFecha(ultimaFecha);

            String programacion = r.getTipoProgramacion();
            boolean porKm = programacion.equals("KILOMETRAJE") || programacion.equals("AMBOS");
            boolean porFecha = programacion.equals("FECHA") || programacion.equals("AMBOS");

            if (porKm && r.getIntervaloKilometraje() != null) {
                int proximoKm = ultimoKilometraje + r.getIntervaloKilometraje();
                p.setProximoKilometraje(proximoKm);
                p.setKilometrosRestantes(Math.max(0, proximoKm - ultimoKilometraje));
            }

            if (porFecha && r.getIntervaloDias() != null) {
                LocalDate proximaFecha = ultimaFecha.plusDays(r.getIntervaloDias());
                p.setProximaFecha(proximaFecha);
                p.setDiasRestantes(Math.max(0, ChronoUnit.DAYS.between(LocalDate.now(), proximaFecha)));
            }

            p.setAlerta(calcularAlerta(p));
            resultados.add(p);
        }

        resultados.sort(Comparator.comparing(ProximoMantenimientoResponse::getProximoKilometraje,
                Comparator.nullsLast(Comparator.naturalOrder())));
        return resultados;
    }

    private String obtenerUsuarioActual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SISTEMA";
    }

    private String calcularAlerta(ProximoMantenimientoResponse p) {
        if (p.getKilometrosRestantes() != null && p.getKilometrosRestantes() <= 500) return "PROXIMO";
        if (p.getDiasRestantes() != null && p.getDiasRestantes() <= 15) return "PROXIMO";
        if (p.getKilometrosRestantes() != null && p.getKilometrosRestantes() <= 1000) return "PRONTO";
        if (p.getDiasRestantes() != null && p.getDiasRestantes() <= 30) return "PRONTO";
        return "NORMAL";
    }

    private MantenimientoRecomendacionResponse toResponse(MantenimientoRecomendacion r) {
        MantenimientoRecomendacionResponse res = new MantenimientoRecomendacionResponse();
        res.setId(r.getId());
        res.setVehiculoId(r.getVehiculo().getId());
        res.setVehiculoPlaca(r.getVehiculo().getPlaca());
        res.setTipo(r.getTipo());
        res.setDescripcion(r.getDescripcion());
        res.setTipoProgramacion(r.getTipoProgramacion());
        res.setIntervaloKilometraje(r.getIntervaloKilometraje());
        res.setIntervaloDias(r.getIntervaloDias());
        res.setActivo(r.isActivo());
        return res;
    }
}
