package com.serviteca.usuario.service;

import com.serviteca.rol.entity.Rol;
import com.serviteca.rol.repository.RolRepository;
import com.serviteca.shared.dto.PagedResponse;
import com.serviteca.shared.exception.DuplicateResourceException;
import com.serviteca.shared.exception.ResourceNotFoundException;
import com.serviteca.usuario.dto.UsuarioRequest;
import com.serviteca.usuario.dto.UsuarioResponse;
import com.serviteca.usuario.dto.UsuarioUpdateRequest;
import com.serviteca.usuario.entity.Usuario;
import com.serviteca.usuario.mapper.UsuarioMapper;
import com.serviteca.usuario.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                          UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public PagedResponse<UsuarioResponse> findAll(int page, int size, String sort) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sort != null ? sort : "username"));
        Page<Usuario> usuarios = usuarioRepository.findByActivoTrue(pageRequest);
        return toPagedResponse(usuarios);
    }

    public List<UsuarioResponse> findAllSimple() {
        return usuarioRepository.findByActivoTrue(PageRequest.of(0, Integer.MAX_VALUE, Sort.by("username"))).stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    public UsuarioResponse findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
        return usuarioMapper.toResponse(usuario);
    }

    public UsuarioResponse create(UsuarioRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("El usuario '" + request.getUsername() + "' ya existe");
        }

        Rol rol = rolRepository.findById(request.getRolId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol", request.getRolId()));

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setRol(rol);
        usuario.setActivo(request.isActivo());

        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(usuario);
    }

    public UsuarioResponse update(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));

        Rol rol = rolRepository.findById(request.getRolId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol", request.getRolId()));

        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setRol(rol);
        usuario.setActivo(request.isActivo());

        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(usuario);
    }

    public void delete(Long id, String motivo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
        usuario.marcarComoEliminado(obtenerUsuarioActual(), motivo);
        usuarioRepository.save(usuario);
    }

    public UsuarioResponse getCurrentUser(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", username));
        return usuarioMapper.toResponse(usuario);
    }

    private String obtenerUsuarioActual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SISTEMA";
    }

    private PagedResponse<UsuarioResponse> toPagedResponse(Page<Usuario> page) {
        return new PagedResponse<>(
                page.getContent().stream().map(usuarioMapper::toResponse).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(),
                page.getTotalPages(), page.isLast()
        );
    }
}
