package com.serviteca.caja.controller;

import com.serviteca.caja.dto.PagoRequest;
import com.serviteca.caja.dto.PagoResponse;
import com.serviteca.caja.service.PagoService;
import com.serviteca.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PagoResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.success(pagoService.listarPagos()));
    }

    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<ApiResponse<List<PagoResponse>>> listarPorOrden(@PathVariable Long ordenId) {
        return ResponseEntity.ok(ApiResponse.success(pagoService.listarPagosPorOrden(ordenId)));
    }

    @GetMapping("/orden/{ordenId}/saldo")
    public ResponseEntity<ApiResponse<BigDecimal>> saldoPendiente(@PathVariable Long ordenId) {
        return ResponseEntity.ok(ApiResponse.success(pagoService.consultarSaldoPendiente(ordenId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PagoResponse>> registrar(@Valid @RequestBody PagoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pago registrado exitosamente", pagoService.registrarPago(request)));
    }

    @PostMapping("/{id}/anular")
    public ResponseEntity<ApiResponse<PagoResponse>> anular(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Pago anulado exitosamente", pagoService.anularPago(id)));
    }
}
