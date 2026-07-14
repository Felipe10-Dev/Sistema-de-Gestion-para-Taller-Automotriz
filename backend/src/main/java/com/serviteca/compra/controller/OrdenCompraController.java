package com.serviteca.compra.controller;

import com.serviteca.compra.dto.*;
import com.serviteca.compra.service.OrdenCompraService;
import com.serviteca.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras")
public class OrdenCompraController {

    private final OrdenCompraService ordenCompraService;

    public OrdenCompraController(OrdenCompraService ordenCompraService) {
        this.ordenCompraService = ordenCompraService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrdenCompraResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(ordenCompraService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrdenCompraResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(ordenCompraService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrdenCompraResponse>> create(
            @Valid @RequestBody OrdenCompraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Orden de compra creada exitosamente",
                        ordenCompraService.create(request)));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<OrdenCompraResponse>> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambioEstadoCompraRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Estado actualizado exitosamente",
                ordenCompraService.cambiarEstado(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id,
                                                    @RequestParam(defaultValue = "Eliminaci\u00f3n manual") String motivo) {
        ordenCompraService.delete(id, motivo);
        return ResponseEntity.ok(ApiResponse.success("Orden de compra eliminada exitosamente"));
    }

    @GetMapping("/recomendaciones")
    public ResponseEntity<ApiResponse<List<RecomendacionCompraResponse>>> recomendaciones() {
        return ResponseEntity.ok(ApiResponse.success(ordenCompraService.obtenerRecomendaciones()));
    }

    @GetMapping("/historial-producto/{productoId}")
    public ResponseEntity<ApiResponse<List<HistorialCompraProductoResponse>>> historialProducto(
            @PathVariable Long productoId) {
        return ResponseEntity.ok(ApiResponse.success(
                ordenCompraService.obtenerHistorialProducto(productoId)));
    }

    @GetMapping("/estadisticas-proveedor/{proveedorId}")
    public ResponseEntity<ApiResponse<EstadisticasProveedorResponse>> estadisticasProveedor(
            @PathVariable Long proveedorId) {
        return ResponseEntity.ok(ApiResponse.success(
                ordenCompraService.obtenerEstadisticasProveedor(proveedorId)));
    }
}
