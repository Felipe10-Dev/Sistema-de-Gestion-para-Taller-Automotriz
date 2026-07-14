package com.serviteca.crm.controller;

import com.serviteca.crm.dto.*;
import com.serviteca.crm.service.CrmService;
import com.serviteca.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crm")
public class CrmController {

    private final CrmService crmService;

    public CrmController(CrmService crmService) {
        this.crmService = crmService;
    }

    @GetMapping("/clientes/{id}/perfil")
    public ResponseEntity<ApiResponse<ClientePerfilResponse>> perfil(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(crmService.obtenerPerfil(id)));
    }

    @GetMapping("/clientes/{id}/economico")
    public ResponseEntity<ApiResponse<HistorialEconomicoResponse>> economico(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(crmService.obtenerHistorialEconomico(id)));
    }

    @GetMapping("/clientes/{id}/ordenes")
    public ResponseEntity<ApiResponse<List<OrdenClienteItem>>> ordenes(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(crmService.obtenerHistorialOrdenes(id)));
    }

    @GetMapping("/clientes/{id}/notas")
    public ResponseEntity<ApiResponse<List<ClienteNotaResponse>>> notas(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(crmService.obtenerNotas(id)));
    }

    @PostMapping("/clientes/{id}/notas")
    public ResponseEntity<ApiResponse<ClienteNotaResponse>> agregarNota(
            @PathVariable Long id, @Valid @RequestBody ClienteNotaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Nota agregada exitosamente", crmService.agregarNota(id, request)));
    }

    @GetMapping("/clientes/{id}/proximos-mantenimientos")
    public ResponseEntity<ApiResponse<List<ProximoMantenimientoClienteResponse>>> proximosMantenimientos(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(crmService.obtenerProximosMantenimientos(id)));
    }

    @GetMapping("/clientes/inactivos")
    public ResponseEntity<ApiResponse<List<ClienteInactivoResponse>>> clientesInactivos(
            @RequestParam(defaultValue = "12") int meses) {
        return ResponseEntity.ok(ApiResponse.success(crmService.obtenerClientesInactivos(meses)));
    }

    @GetMapping("/clientes/vip")
    public ResponseEntity<ApiResponse<List<ClienteInactivoResponse>>> clientesVip() {
        return ResponseEntity.ok(ApiResponse.success(crmService.obtenerClientesVip()));
    }

    @GetMapping("/ranking")
    public ResponseEntity<ApiResponse<List<RankingItem>>> ranking(
            @RequestParam(defaultValue = "invertido") String tipo) {
        return ResponseEntity.ok(ApiResponse.success(crmService.obtenerRanking(tipo)));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<CrmDashboardResponse>> dashboard() {
        return ResponseEntity.ok(ApiResponse.success(crmService.obtenerDashboardCrm()));
    }
}
