package com.serviteca.vehiculo.controller;

import com.serviteca.shared.dto.ApiResponse;
import com.serviteca.shared.dto.PagedResponse;
import com.serviteca.vehiculo.dto.VehiculoRequest;
import com.serviteca.vehiculo.dto.VehiculoResponse;
import com.serviteca.vehiculo.historial.dto.EstadisticasVehiculo;
import com.serviteca.vehiculo.historial.dto.EventoLineaTiempo;
import com.serviteca.vehiculo.historial.dto.HistorialVehiculoItem;
import com.serviteca.vehiculo.mantenimiento.dto.MantenimientoRecomendacionRequest;
import com.serviteca.vehiculo.mantenimiento.dto.MantenimientoRecomendacionResponse;
import com.serviteca.vehiculo.mantenimiento.dto.ProximoMantenimientoResponse;
import com.serviteca.vehiculo.mantenimiento.service.MantenimientoService;
import com.serviteca.vehiculo.service.HistorialVehiculoService;
import com.serviteca.vehiculo.service.VehiculoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;
    private final HistorialVehiculoService historialService;
    private final MantenimientoService mantenimientoService;

    public VehiculoController(VehiculoService vehiculoService,
                              HistorialVehiculoService historialService,
                              MantenimientoService mantenimientoService) {
        this.vehiculoService = vehiculoService;
        this.historialService = historialService;
        this.mantenimientoService = mantenimientoService;
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<VehiculoResponse>>> findAllSimple() {
        return ResponseEntity.ok(ApiResponse.success(vehiculoService.findAllSimple()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<VehiculoResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "placa") String sort,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String modelo,
            @RequestParam(required = false) Integer anio) {
        return ResponseEntity.ok(ApiResponse.success(vehiculoService.findAll(page, size, sort, search, marca, modelo, anio)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehiculoResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(vehiculoService.findById(id)));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<List<VehiculoResponse>>> findByCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(ApiResponse.success(vehiculoService.findByClienteId(clienteId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VehiculoResponse>> create(@Valid @RequestBody VehiculoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Veh\u00edculo creado exitosamente", vehiculoService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VehiculoResponse>> update(@PathVariable Long id,
                                                                 @Valid @RequestBody VehiculoRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Veh\u00edculo actualizado exitosamente",
                vehiculoService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id,
                                                    @RequestParam(defaultValue = "Eliminaci\u00f3n manual") String motivo) {
        vehiculoService.delete(id, motivo);
        return ResponseEntity.ok(ApiResponse.success("Veh\u00edculo eliminado exitosamente"));
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<ApiResponse<List<HistorialVehiculoItem>>> historial(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(historialService.obtenerHistorial(id)));
    }

    @GetMapping("/{id}/linea-tiempo")
    public ResponseEntity<ApiResponse<List<EventoLineaTiempo>>> lineaTiempo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(historialService.obtenerLineaTiempo(id)));
    }

    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<ApiResponse<EstadisticasVehiculo>> estadisticas(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(historialService.obtenerEstadisticas(id)));
    }

    @GetMapping("/{id}/mantenimientos")
    public ResponseEntity<ApiResponse<List<MantenimientoRecomendacionResponse>>> listarMantenimientos(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(mantenimientoService.listarPorVehiculo(id)));
    }

    @PostMapping("/{id}/mantenimientos")
    public ResponseEntity<ApiResponse<MantenimientoRecomendacionResponse>> crearMantenimiento(
            @PathVariable Long id,
            @Valid @RequestBody MantenimientoRecomendacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Recomendaci\u00f3n creada exitosamente",
                        mantenimientoService.crear(id, request)));
    }

    @PutMapping("/mantenimientos/{recomendacionId}")
    public ResponseEntity<ApiResponse<MantenimientoRecomendacionResponse>> actualizarMantenimiento(
            @PathVariable Long recomendacionId,
            @Valid @RequestBody MantenimientoRecomendacionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Recomendaci\u00f3n actualizada exitosamente",
                mantenimientoService.actualizar(recomendacionId, request)));
    }

    @DeleteMapping("/mantenimientos/{recomendacionId}")
    public ResponseEntity<ApiResponse<Void>> eliminarMantenimiento(@PathVariable Long recomendacionId,
                                                                    @RequestParam(defaultValue = "Eliminaci\u00f3n manual") String motivo) {
        mantenimientoService.eliminar(recomendacionId, motivo);
        return ResponseEntity.ok(ApiResponse.success("Recomendaci\u00f3n eliminada exitosamente"));
    }

    @GetMapping("/{id}/proximos-mantenimientos")
    public ResponseEntity<ApiResponse<List<ProximoMantenimientoResponse>>> proximosMantenimientos(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(mantenimientoService.calcularProximos(id)));
    }
}
