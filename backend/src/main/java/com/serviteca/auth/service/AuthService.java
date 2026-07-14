package com.serviteca.auth.service;

import com.serviteca.auth.dto.LoginRequest;
import com.serviteca.auth.dto.LoginResponse;
import com.serviteca.auth.dto.RefreshTokenRequest;
import com.serviteca.auth.dto.RegisterRequest;
import com.serviteca.rol.entity.Rol;
import com.serviteca.rol.repository.RolRepository;
import com.serviteca.security.jwt.JwtTokenProvider;
import com.serviteca.shared.exception.BadRequestException;
import com.serviteca.shared.exception.DuplicateResourceException;
import com.serviteca.shared.exception.ResourceNotFoundException;
import com.serviteca.usuario.entity.Usuario;
import com.serviteca.usuario.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider,
                       UsuarioRepository usuarioRepository, RolRepository rolRepository,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", request.getUsername()));

        String accessToken = tokenProvider.generateAccessToken(usuario.getUsername(), usuario.getRol().getNombre(),
                usuario.getEmpresaId(), usuario.getSedeId());
        String refreshToken = tokenProvider.generateRefreshToken(usuario.getUsername());

        return new LoginResponse(accessToken, refreshToken, usuario.getUsername(),
                usuario.getRol().getNombre(), usuario.getNombre(),
                usuario.getEmpresaId(), usuario.getSedeId());
    }

    public LoginResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("El usuario '" + request.getUsername() + "' ya existe");
        }

        Rol rol = rolRepository.findByNombre("OPERADOR")
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "nombre", "OPERADOR"));

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setRol(rol);
        usuario.setActivo(true);
        usuario.setEmpresaId(1L);
        usuario.setSedeId(1L);

        usuarioRepository.save(usuario);

        String accessToken = tokenProvider.generateAccessToken(usuario.getUsername(), usuario.getRol().getNombre(),
                usuario.getEmpresaId(), usuario.getSedeId());
        String refreshToken = tokenProvider.generateRefreshToken(usuario.getUsername());

        return new LoginResponse(accessToken, refreshToken, usuario.getUsername(),
                usuario.getRol().getNombre(), usuario.getNombre(),
                usuario.getEmpresaId(), usuario.getSedeId());
    }

    public LoginResponse refreshToken(RefreshTokenRequest request) {
        if (!tokenProvider.validateToken(request.getRefreshToken())) {
            throw new BadRequestException("Refresh token inv\u00e1lido o expirado");
        }

        String username = tokenProvider.getUsernameFromToken(request.getRefreshToken());
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", username));

        String newAccessToken = tokenProvider.generateAccessToken(username, usuario.getRol().getNombre(),
                usuario.getEmpresaId(), usuario.getSedeId());
        String newRefreshToken = tokenProvider.generateRefreshToken(username);

        return new LoginResponse(newAccessToken, newRefreshToken, username,
                usuario.getRol().getNombre(), usuario.getNombre(),
                usuario.getEmpresaId(), usuario.getSedeId());
    }
}
