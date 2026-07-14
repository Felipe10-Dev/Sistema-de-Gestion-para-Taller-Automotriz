package com.serviteca.orden.controller;

import com.serviteca.orden.dto.CambioEstadoRequest;
import com.serviteca.orden.dto.ObservacionRequest;
import com.serviteca.orden.dto.OrdenTrabajoRequest;
import com.serviteca.orden.dto.OrdenTrabajoResponse;
import com.serviteca.orden.service.OrdenTrabajoService;
import com.serviteca.shared.dto.ApiResponse;
import com.serviteca.shared.dto.PagedResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenTrabajoController {

    private final OrdenTrabajoService ordenTrabajoService;

    public OrdenTrabajoController(OrdenTrabajoService ordenTrabajoService) {
        this.ordenTrabajoService = ordenTrabajoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<OrdenTrabajoResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaIngreso") String sort,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String estadoFinanciero,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long vehiculoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(ApiResponse.success(
                ordenTrabajoService.findAll(page, size, sort, search, estado, estadoFinanciero, clienteId, vehiculoId, fechaInicio, fechaFin)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrdenTrabajoResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(ordenTrabajoService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrdenTrabajoResponse>> create(@Valid @RequestBody OrdenTrabajoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Orden de trabajo creada exitosamente",
                        ordenTrabajoService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrdenTrabajoResponse>> update(
            @PathVariable Long id, @Valid @RequestBody OrdenTrabajoRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Orden actualizada exitosamente",
                ordenTrabajoService.actualizarOrden(id, request)));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<OrdenTrabajoResponse>> cambiarEstado(
            @PathVariable Long id, @Valid @RequestBody CambioEstadoRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Estado actualizado exitosamente",
                ordenTrabajoService.cambiarEstado(id, request)));
    }

    @PostMapping("/{id}/observaciones")
    public ResponseEntity<ApiResponse<OrdenTrabajoResponse>> agregarObservacion(
            @PathVariable Long id, @Valid @RequestBody ObservacionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Observaci\u00f3n agregada exitosamente",
                ordenTrabajoService.agregarObservacion(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id,
                                                    @RequestParam(defaultValue = "Eliminaci\u00f3n manual") String motivo) {
        ordenTrabajoService.delete(id, motivo);
        return ResponseEntity.ok(ApiResponse.success("Orden de trabajo eliminada exitosamente"));
    }
}
