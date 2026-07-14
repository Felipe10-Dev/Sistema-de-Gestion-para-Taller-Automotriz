package com.serviteca.caja.controller;

import com.serviteca.caja.dto.*;
import com.serviteca.caja.service.CajaService;
import com.serviteca.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/caja")
public class CajaController {

    private final CajaService cajaService;

    public CajaController(CajaService cajaService) {
        this.cajaService = cajaService;
    }

    @GetMapping("/actual")
    public ResponseEntity<ApiResponse<CajaResponse>> obtenerCajaActual() {
        CajaResponse caja = cajaService.obtenerCajaActual();
        if (caja == null) {
            return ResponseEntity.ok(ApiResponse.success("No hay caja abierta", null));
        }
        return ResponseEntity.ok(ApiResponse.success(caja));
    }

    @GetMapping("/historial")
    public ResponseEntity<ApiResponse<List<CajaResponse>>> historial() {
        return ResponseEntity.ok(ApiResponse.success(cajaService.listarHistorial()));
    }

    @PostMapping("/apertura")
    public ResponseEntity<ApiResponse<CajaResponse>> abrirCaja(@Valid @RequestBody AperturaCajaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Caja abierta exitosamente", cajaService.abrirCaja(request)));
    }

    @PostMapping("/{id}/cierre")
    public ResponseEntity<ApiResponse<CajaResponse>> cerrarCaja(
            @PathVariable Long id, @Valid @RequestBody CierreCajaRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Caja cerrada exitosamente", cajaService.cerrarCaja(id, request)));
    }

    @GetMapping("/movimientos")
    public ResponseEntity<ApiResponse<List<MovimientoCajaResponse>>> movimientos(
            @RequestParam(required = false) Long cajaId) {
        return ResponseEntity.ok(ApiResponse.success(cajaService.listarMovimientos(cajaId)));
    }
}
