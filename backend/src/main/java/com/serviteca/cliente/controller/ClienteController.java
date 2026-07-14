package com.serviteca.cliente.controller;

import com.serviteca.cliente.dto.ClienteRequest;
import com.serviteca.cliente.dto.ClienteResponse;
import com.serviteca.cliente.service.ClienteService;
import com.serviteca.shared.dto.ApiResponse;
import com.serviteca.shared.dto.PagedResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ClienteResponse>>> findAllSimple() {
        return ResponseEntity.ok(ApiResponse.success(clienteService.findAllSimple()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ClienteResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String sort,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String filtro) {
        return ResponseEntity.ok(ApiResponse.success(clienteService.findAll(page, size, sort, search, filtro)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(clienteService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClienteResponse>> create(@Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cliente creado exitosamente", clienteService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponse>> update(@PathVariable Long id,
                                                                @Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cliente actualizado exitosamente", clienteService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id,
                                                    @RequestParam(defaultValue = "Eliminaci\u00f3n manual") String motivo) {
        clienteService.delete(id, motivo);
        return ResponseEntity.ok(ApiResponse.success("Cliente eliminado exitosamente"));
    }
}
