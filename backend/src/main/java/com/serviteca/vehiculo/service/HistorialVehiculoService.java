package com.serviteca.vehiculo.service;

import com.serviteca.orden.entity.OrdenTrabajo;
import com.serviteca.orden.repository.OrdenProductoRepository;
import com.serviteca.orden.repository.OrdenServicioRepository;
import com.serviteca.orden.repository.OrdenTrabajoRepository;
import com.serviteca.shared.exception.ResourceNotFoundException;
import com.serviteca.vehiculo.entity.Vehiculo;
import com.serviteca.vehiculo.historial.dto.EstadisticasVehiculo;
import com.serviteca.vehiculo.historial.dto.EventoLineaTiempo;
import com.serviteca.vehiculo.historial.dto.HistorialVehiculoItem;
import com.serviteca.vehiculo.repository.VehiculoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HistorialVehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final OrdenTrabajoRepository ordenRepository;
    private final OrdenServicioRepository ordenServicioRepository;
    private final OrdenProductoRepository ordenProductoRepository;

    public HistorialVehiculoService(VehiculoRepository vehiculoRepository,
                                    OrdenTrabajoRepository ordenRepository,
                                    OrdenServicioRepository ordenServicioRepository,
                                    OrdenProductoRepository ordenProductoRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.ordenRepository = ordenRepository;
        this.ordenServicioRepository = ordenServicioRepository;
        this.ordenProductoRepository = ordenProductoRepository;
    }

    public List<HistorialVehiculoItem> obtenerHistorial(Long vehiculoId) {
        vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new ResourceNotFoundException("Veh\u00edculo", vehiculoId));

        List<OrdenTrabajo> ordenes = ordenRepository.findByVehiculoIdOrderByFechaIngresoDesc(vehiculoId);
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

        List<HistorialVehiculoItem> items = new ArrayList<>();
        for (OrdenTrabajo orden : ordenes) {
            HistorialVehiculoItem item = new HistorialVehiculoItem();
            item.setOrdenId(orden.getId());
            item.setNumeroOrden(orden.getNumeroOrden());
            item.setFecha(orden.getFechaIngreso());
            item.setEstado(orden.getEstado());
            item.setEstadoFinanciero(orden.getEstadoFinanciero());
            item.setTotalGeneral(orden.getTotalGeneral());
            item.setObservaciones(orden.getObservaciones());
            item.setKilometraje(orden.getKilometraje());
            item.setTecnicoNombre(orden.getTecnico() != null ?
                    orden.getTecnico().getNombre() + " " + orden.getTecnico().getApellido() : null);
            item.setServiciosRealizados(serviciosPorOrden.getOrDefault(orden.getId(), List.of()));
            item.setProductosUtilizados(productosPorOrden.getOrDefault(orden.getId(), List.of()));
            items.add(item);
        }
        return items;
    }

    public List<EventoLineaTiempo> obtenerLineaTiempo(Long vehiculoId) {
        List<HistorialVehiculoItem> historial = obtenerHistorial(vehiculoId);
        List<EventoLineaTiempo> eventos = new ArrayList<>();

        for (HistorialVehiculoItem item : historial) {
            EventoLineaTiempo evento = new EventoLineaTiempo();
            evento.setFecha(item.getFecha());
            evento.setTipo(item.getEstado());
            evento.setDescripcion("Orden " + item.getNumeroOrden());
            evento.setKilometraje(item.getKilometraje());
            evento.setTotalOrden(item.getTotalGeneral());
            evento.setObservaciones(item.getObservaciones());
            eventos.add(evento);
        }

        eventos.sort(Comparator.comparing(EventoLineaTiempo::getFecha).reversed());
        return eventos;
    }

    public EstadisticasVehiculo obtenerEstadisticas(Long vehiculoId) {
        vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new ResourceNotFoundException("Veh\u00edculo", vehiculoId));

        EstadisticasVehiculo stats = new EstadisticasVehiculo();

        long totalVisitas = ordenRepository.countByVehiculoId(vehiculoId);
        stats.setTotalVisitas(totalVisitas);

        stats.setTotalInvertido(ordenRepository.sumTotalGeneralByVehiculoId(vehiculoId));

        LocalDateTime ultimaVisita = ordenRepository.findMaxFechaIngresoByVehiculoId(vehiculoId);
        stats.setUltimaVisita(ultimaVisita);

        stats.setPromedioDiasEntreVisitas(calcularPromedioDias(vehiculoId, totalVisitas));

        stats.setServicioMasFrecuente(calcularServicioMasFrecuente(vehiculoId));
        stats.setProductoMasUtilizado(calcularProductoMasUtilizado(vehiculoId));

        return stats;
    }

    private double calcularPromedioDias(Long vehiculoId, long totalVisitas) {
        if (totalVisitas < 2) return 0;
        List<OrdenTrabajo> ordenes = ordenRepository.findByVehiculoIdOrderByFechaIngresoDesc(vehiculoId);
        if (ordenes.size() < 2) return 0;

        long sumaDias = 0;
        for (int i = 0; i < ordenes.size() - 1; i++) {
            sumaDias += ChronoUnit.DAYS.between(
                    ordenes.get(i + 1).getFechaIngreso(),
                    ordenes.get(i).getFechaIngreso());
        }
        return (double) sumaDias / (ordenes.size() - 1);
    }

    private String calcularServicioMasFrecuente(Long vehiculoId) {
        List<OrdenTrabajo> ordenes = ordenRepository.findByVehiculoIdOrderByFechaIngresoDesc(vehiculoId);
        if (ordenes.isEmpty()) return null;
        List<Long> ids = ordenes.stream().map(OrdenTrabajo::getId).toList();
        Map<String, Long> frecuencia = ordenServicioRepository.findByOrdenIdIn(ids).stream()
                .collect(Collectors.groupingBy(
                        os -> os.getServicio().getNombre(),
                        Collectors.counting()));
        return frecuencia.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private String calcularProductoMasUtilizado(Long vehiculoId) {
        List<OrdenTrabajo> ordenes = ordenRepository.findByVehiculoIdOrderByFechaIngresoDesc(vehiculoId);
        if (ordenes.isEmpty()) return null;
        List<Long> ids = ordenes.stream().map(OrdenTrabajo::getId).toList();
        Map<String, Long> frecuencia = ordenProductoRepository.findByOrdenIdIn(ids).stream()
                .collect(Collectors.groupingBy(
                        op -> op.getProducto().getNombre(),
                        Collectors.counting()));
        return frecuencia.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}