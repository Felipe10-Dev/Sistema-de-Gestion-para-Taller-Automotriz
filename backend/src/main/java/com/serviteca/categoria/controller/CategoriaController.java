package com.serviteca.categoria.controller;

import com.serviteca.categoria.dto.CategoriaRequest;
import com.serviteca.categoria.dto.CategoriaResponse;
import com.serviteca.categoria.service.CategoriaService;
import com.serviteca.shared.dto.ApiResponse;
import com.serviteca.shared.dto.PagedResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<CategoriaResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String sort) {
        return ResponseEntity.ok(ApiResponse.success(categoriaService.findAll(page, size, sort)));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CategoriaResponse>>> findAllSimple() {
        return ResponseEntity.ok(ApiResponse.success(categoriaService.findAllSimple()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(categoriaService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoriaResponse>> create(@Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Categor\u00eda creada exitosamente", categoriaService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponse>> update(@PathVariable Long id,
            @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Categor\u00eda actualizada exitosamente",
                categoriaService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id,
                                                    @RequestParam(defaultValue = "Eliminaci\u00f3n manual") String motivo) {
        categoriaService.delete(id, motivo);
        return ResponseEntity.ok(ApiResponse.success("Categor\u00eda eliminada exitosamente"));
    }
}
