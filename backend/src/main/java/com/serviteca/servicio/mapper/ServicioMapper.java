package com.serviteca.servicio.mapper;

import com.serviteca.servicio.dto.ServicioResponse;
import com.serviteca.servicio.entity.Servicio;
import org.springframework.stereotype.Component;

@Component
public class ServicioMapper {

    public ServicioResponse toResponse(Servicio servicio) {
        return new ServicioResponse(
                servicio.getId(),
                servicio.getNombre(),
                servicio.getDescripcion(),
                servicio.getPrecio(),
                servicio.getDuracionEstimadaMinutos(),
                servicio.getCategoria() != null ? servicio.getCategoria().getId() : null,
                servicio.getCategoria() != null ? servicio.getCategoria().getNombre() : null,
                servicio.isActivo()
        );
    }
}
