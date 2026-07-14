package com.serviteca.servicio.controller;

import com.serviteca.servicio.dto.ServicioRequest;
import com.serviteca.servicio.dto.ServicioResponse;
import com.serviteca.servicio.service.ServicioService;
import com.serviteca.shared.dto.ApiResponse;
import com.serviteca.shared.dto.PagedResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    private final ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ServicioResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String sort,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success(servicioService.findAll(page, size, sort, search)));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ServicioResponse>>> findAllSimple(
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success(servicioService.findAllSimple(search)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServicioResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(servicioService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ServicioResponse>> create(@Valid @RequestBody ServicioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Servicio creado exitosamente", servicioService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServicioResponse>> update(@PathVariable Long id,
                                                                 @Valid @RequestBody ServicioRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Servicio actualizado exitosamente",
                servicioService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id,
                                                    @RequestParam(defaultValue = "Eliminaci\u00f3n manual") String motivo) {
        servicioService.delete(id, motivo);
        return ResponseEntity.ok(ApiResponse.success("Servicio eliminado exitosamente"));
    }
}
