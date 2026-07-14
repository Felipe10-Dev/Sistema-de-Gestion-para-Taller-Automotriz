package com.serviteca.rol.mapper;

import com.serviteca.rol.dto.RolRequest;
import com.serviteca.rol.dto.RolResponse;
import com.serviteca.rol.entity.Rol;
import org.springframework.stereotype.Component;

@Component
public class RolMapper {

    public RolResponse toResponse(Rol rol) {
        return new RolResponse(rol.getId(), rol.getNombre(), rol.getDescripcion());
    }

    public Rol toEntity(RolRequest request) {
        return new Rol(request.getNombre(), request.getDescripcion());
    }
}
