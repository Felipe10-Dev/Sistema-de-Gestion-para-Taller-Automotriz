package com.serviteca.usuario.mapper;

import com.serviteca.usuario.dto.UsuarioResponse;
import com.serviteca.usuario.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol().getNombre(),
                usuario.isActivo()
        );
    }
}
