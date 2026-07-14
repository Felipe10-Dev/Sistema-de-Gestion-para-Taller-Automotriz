package com.serviteca.producto.controller;

import com.serviteca.producto.dto.ProductoRequest;
import com.serviteca.producto.dto.ProductoResponse;
import com.serviteca.producto.service.ProductoService;
import com.serviteca.shared.dto.ApiResponse;
import com.serviteca.shared.dto.PagedResponse;
import jakarta.validation.Valid;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProductoResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String sort,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Long proveedorId,
            @RequestParam(defaultValue = "false") boolean bajoStock,
            @RequestParam(defaultValue = "false") boolean sinStock) {
        return ResponseEntity.ok(ApiResponse.success(
                productoService.findAll(page, size, sort, search, categoriaId, proveedorId, bajoStock, sinStock)));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ProductoResponse>>> findAllSimple(
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success(productoService.findAllSimple(search)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productoService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductoResponse>> create(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Producto creado exitosamente", productoService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponse>> update(@PathVariable Long id,
                                                                 @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Producto actualizado exitosamente",
                productoService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id,
                                                    @RequestParam(defaultValue = "Eliminaci\u00f3n manual") String motivo) {
        productoService.delete(id, motivo);
        return ResponseEntity.ok(ApiResponse.success("Producto eliminado exitosamente"));
    }
}
