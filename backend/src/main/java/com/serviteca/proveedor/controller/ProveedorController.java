package com.serviteca.proveedor.controller;

import com.serviteca.proveedor.dto.ProveedorRequest;
import com.serviteca.proveedor.dto.ProveedorResponse;
import com.serviteca.proveedor.service.ProveedorService;
import com.serviteca.shared.dto.ApiResponse;
import com.serviteca.shared.dto.PagedResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProveedorResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String sort,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success(proveedorService.findAll(page, size, sort, search)));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ProveedorResponse>>> findAllSimple(
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success(proveedorService.findAllSimple(search)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProveedorResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(proveedorService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProveedorResponse>> create(@Valid @RequestBody ProveedorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Proveedor creado exitosamente", proveedorService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProveedorResponse>> update(@PathVariable Long id,
                                                                  @Valid @RequestBody ProveedorRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Proveedor actualizado exitosamente",
                proveedorService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id,
                                                    @RequestParam(defaultValue = "Eliminaci\u00f3n manual") String motivo) {
        proveedorService.delete(id, motivo);
        return ResponseEntity.ok(ApiResponse.success("Proveedor eliminado exitosamente"));
    }
}
