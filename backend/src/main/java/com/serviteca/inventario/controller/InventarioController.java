package com.serviteca.inventario.controller;

import com.serviteca.inventario.dto.InventarioRequest;
import com.serviteca.inventario.dto.InventarioResponse;
import com.serviteca.inventario.dto.MovimientoRequest;
import com.serviteca.inventario.dto.MovimientoResponse;
import com.serviteca.inventario.service.InventarioService;
import com.serviteca.shared.dto.ApiResponse;
import com.serviteca.shared.dto.PagedResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<InventarioResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {
        return ResponseEntity.ok(ApiResponse.success(inventarioService.findAll(page, size, sort)));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<InventarioResponse>>> findAllSimple() {
        return ResponseEntity.ok(ApiResponse.success(inventarioService.findAllSimple()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventarioResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(inventarioService.findById(id)));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<ApiResponse<InventarioResponse>> findByProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(ApiResponse.success(inventarioService.findByProductoId(productoId)));
    }

    @GetMapping("/bajo-stock")
    public ResponseEntity<ApiResponse<List<InventarioResponse>>> findBajoStock() {
        return ResponseEntity.ok(ApiResponse.success(inventarioService.findBajoStock()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InventarioResponse>> create(@Valid @RequestBody InventarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registro de inventario creado", inventarioService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InventarioResponse>> update(@PathVariable Long id,
                                                                   @Valid @RequestBody InventarioRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Inventario actualizado", inventarioService.update(id, request)));
    }

    @PostMapping("/movimiento")
    public ResponseEntity<ApiResponse<MovimientoResponse>> registrarMovimiento(
            @Valid @RequestBody MovimientoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Movimiento registrado exitosamente",
                        inventarioService.registrarMovimiento(request)));
    }

    @GetMapping("/movimientos/{productoId}")
    public ResponseEntity<ApiResponse<List<MovimientoResponse>>> getMovimientos(@PathVariable Long productoId) {
        return ResponseEntity.ok(ApiResponse.success(inventarioService.getMovimientos(productoId)));
    }
}
