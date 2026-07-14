package com.serviteca.caja.controller;

import com.serviteca.caja.dto.MetodoPagoRequest;
import com.serviteca.caja.dto.MetodoPagoResponse;
import com.serviteca.caja.service.MetodoPagoService;
import com.serviteca.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metodos-pago")
public class MetodoPagoController {

    private final MetodoPagoService service;

    public MetodoPagoController(MetodoPagoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MetodoPagoResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.success(service.listar()));
    }

    @GetMapping("/todos")
    public ResponseEntity<ApiResponse<List<MetodoPagoResponse>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.success(service.listarTodos()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MetodoPagoResponse>> crear(@Valid @RequestBody MetodoPagoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("M\u00e9todo de pago creado exitosamente", service.crear(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MetodoPagoResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody MetodoPagoRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.actualizar(id, request)));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleActivo(@PathVariable Long id) {
        service.toggleActivo(id);
        return ResponseEntity.ok(ApiResponse.success("Estado cambiado exitosamente"));
    }
}
