package com.serviteca.auth.controller;

import com.serviteca.auth.dto.LoginRequest;
import com.serviteca.auth.dto.LoginResponse;
import com.serviteca.auth.dto.RefreshTokenRequest;
import com.serviteca.auth.dto.RegisterRequest;
import com.serviteca.auth.service.AuthService;
import com.serviteca.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Inicio de sesi\u00f3n exitoso", response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Usuario registrado exitosamente", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token renovado exitosamente", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Object>> me() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return ResponseEntity.ok(ApiResponse.success("No autenticado", null));
        }
        return ResponseEntity.ok(ApiResponse.success("Autenticado",
            java.util.Map.of("name", auth.getName(), "authorities", auth.getAuthorities(), "authClass", auth.getClass().getName())));
    }
}
