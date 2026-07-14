package com.serviteca.rol.service;

import com.serviteca.rol.dto.RolRequest;
import com.serviteca.rol.dto.RolResponse;
import com.serviteca.rol.entity.Rol;
import com.serviteca.rol.mapper.RolMapper;
import com.serviteca.rol.repository.RolRepository;
import com.serviteca.shared.exception.DuplicateResourceException;
import com.serviteca.shared.exception.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RolService {

    private final RolRepository rolRepository;
    private final RolMapper rolMapper;

    public RolService(RolRepository rolRepository, RolMapper rolMapper) {
        this.rolRepository = rolRepository;
        this.rolMapper = rolMapper;
    }

    public List<RolResponse> findAll() {
        return rolRepository.findAll().stream()
                .map(rolMapper::toResponse)
                .toList();
    }

    public RolResponse findById(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", id));
        return rolMapper.toResponse(rol);
    }

    public RolResponse create(RolRequest request) {
        if (rolRepository.existsByNombre(request.getNombre())) {
            throw new DuplicateResourceException("El rol '" + request.getNombre() + "' ya existe");
        }
        Rol rol = rolMapper.toEntity(request);
        rol = rolRepository.save(rol);
        return rolMapper.toResponse(rol);
    }

    public RolResponse update(Long id, RolRequest request) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", id));
        rol.setNombre(request.getNombre());
        rol.setDescripcion(request.getDescripcion());
        rol = rolRepository.save(rol);
        return rolMapper.toResponse(rol);
    }

    public void delete(Long id, String motivo) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", id));
        rol.marcarComoEliminado(obtenerUsuarioActual(), motivo);
        rolRepository.save(rol);
    }

    private String obtenerUsuarioActual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SISTEMA";
    }
}
