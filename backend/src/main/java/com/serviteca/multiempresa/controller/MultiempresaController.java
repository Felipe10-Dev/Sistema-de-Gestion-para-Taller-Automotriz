package com.serviteca.multiempresa.controller;

import com.serviteca.multiempresa.dto.*;
import com.serviteca.multiempresa.service.MultiempresaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/multiempresa")
public class MultiempresaController {

    private final MultiempresaService service;

    public MultiempresaController(MultiempresaService service) {
        this.service = service;
    }

    // --- Empresas ---
    @GetMapping("/empresas")
    public ResponseEntity<List<EmpresaResponse>> listarEmpresas() {
        return ResponseEntity.ok(service.listarEmpresas());
    }

    @GetMapping("/empresas/{id}")
    public ResponseEntity<EmpresaResponse> obtenerEmpresa(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerEmpresa(id));
    }

    @PostMapping("/empresas")
    public ResponseEntity<EmpresaResponse> crearEmpresa(@RequestBody EmpresaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearEmpresa(request));
    }

    @PutMapping("/empresas/{id}")
    public ResponseEntity<EmpresaResponse> actualizarEmpresa(@PathVariable Long id,
                                                              @RequestBody EmpresaRequest request) {
        return ResponseEntity.ok(service.actualizarEmpresa(id, request));
    }

    @DeleteMapping("/empresas/{id}")
    public ResponseEntity<Void> eliminarEmpresa(@PathVariable Long id,
                                                @RequestParam(defaultValue = "Eliminaci\u00f3n manual") String motivo) {
        service.eliminarEmpresa(id, motivo);
        return ResponseEntity.noContent().build();
    }

    // --- Sedes ---
    @GetMapping("/empresas/{empresaId}/sedes")
    public ResponseEntity<List<SedeResponse>> listarSedes(@PathVariable Long empresaId) {
        return ResponseEntity.ok(service.listarSedesPorEmpresa(empresaId));
    }

    @GetMapping("/empresas/{empresaId}/sedes/activas")
    public ResponseEntity<List<SedeResponse>> listarSedesActivas(@PathVariable Long empresaId) {
        return ResponseEntity.ok(service.listarSedesActivas(empresaId));
    }

    @PostMapping("/empresas/{empresaId}/sedes")
    public ResponseEntity<SedeResponse> crearSede(@PathVariable Long empresaId,
                                                   @RequestBody SedeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearSede(empresaId, request));
    }

    @PutMapping("/sedes/{id}")
    public ResponseEntity<SedeResponse> actualizarSede(@PathVariable Long id,
                                                        @RequestBody SedeRequest request) {
        return ResponseEntity.ok(service.actualizarSede(id, request));
    }

    @DeleteMapping("/sedes/{id}")
    public ResponseEntity<Void> eliminarSede(@PathVariable Long id,
                                             @RequestParam(defaultValue = "Eliminaci\u00f3n manual") String motivo) {
        service.eliminarSede(id, motivo);
        return ResponseEntity.noContent().build();
    }

    // --- Transferencias ---
    @GetMapping("/transferencias")
    public ResponseEntity<List<TransferenciaResponse>> listarTransferencias() {
        return ResponseEntity.ok(service.listarTransferencias());
    }

    @PostMapping("/transferencias")
    public ResponseEntity<TransferenciaResponse> crearTransferencia(@RequestBody TransferenciaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearTransferencia(request));
    }

    // --- Dashboard Consolidado ---
    @GetMapping("/dashboard/{empresaId}")
    public ResponseEntity<DashboardConsolidadoResponse> dashboardConsolidado(@PathVariable Long empresaId) {
        return ResponseEntity.ok(service.dashboardConsolidado(empresaId));
    }
}
