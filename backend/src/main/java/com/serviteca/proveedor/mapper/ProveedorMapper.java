package com.serviteca.proveedor.mapper;

import com.serviteca.proveedor.dto.ProveedorResponse;
import com.serviteca.proveedor.entity.Proveedor;
import org.springframework.stereotype.Component;

@Component
public class ProveedorMapper {

    public ProveedorResponse toResponse(Proveedor proveedor) {
        return new ProveedorResponse(
                proveedor.getId(), proveedor.getNit(), proveedor.getNombre(),
                proveedor.getContacto(), proveedor.getTelefono(), proveedor.getEmail(),
                proveedor.getDireccion(), proveedor.isActivo()
        );
    }
}
