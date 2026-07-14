package com.serviteca.usuario.controller;

import com.serviteca.shared.dto.ApiResponse;
import com.serviteca.shared.dto.PagedResponse;
import com.serviteca.usuario.dto.UsuarioRequest;
import com.serviteca.usuario.dto.UsuarioResponse;
import com.serviteca.usuario.dto.UsuarioUpdateRequest;
import com.serviteca.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<UsuarioResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username") String sort) {
        return ResponseEntity.ok(ApiResponse.success(usuarioService.findAll(page, size, sort)));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<UsuarioResponse>>> findAllSimple() {
        return ResponseEntity.ok(ApiResponse.success(usuarioService.findAllSimple()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(usuarioService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioResponse>> create(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Usuario creado exitosamente", usuarioService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> update(@PathVariable Long id,
                                                                @Valid @RequestBody UsuarioUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Usuario actualizado exitosamente", usuarioService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id,
                                                    @RequestParam(defaultValue = "Eliminaci\u00f3n manual") String motivo) {
        usuarioService.delete(id, motivo);
        return ResponseEntity.ok(ApiResponse.success("Usuario eliminado exitosamente"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UsuarioResponse>> me(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(usuarioService.getCurrentUser(userDetails.getUsername())));
    }
}
