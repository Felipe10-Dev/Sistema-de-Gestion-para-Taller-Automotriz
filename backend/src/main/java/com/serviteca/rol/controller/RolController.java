package com.serviteca.rol.controller;

import com.serviteca.rol.dto.RolRequest;
import com.serviteca.rol.dto.RolResponse;
import com.serviteca.rol.service.RolService;
import com.serviteca.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RolResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(rolService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RolResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(rolService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RolResponse>> create(@Valid @RequestBody RolRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Rol creado exitosamente", rolService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RolResponse>> update(@PathVariable Long id,
                                                            @Valid @RequestBody RolRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Rol actualizado exitosamente", rolService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id,
                                                    @RequestParam(defaultValue = "Eliminaci\u00f3n manual") String motivo) {
        rolService.delete(id, motivo);
        return ResponseEntity.ok(ApiResponse.success("Rol eliminado exitosamente"));
    }
}
