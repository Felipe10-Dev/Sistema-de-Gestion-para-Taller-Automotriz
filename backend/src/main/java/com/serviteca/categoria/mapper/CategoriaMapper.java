package com.serviteca.categoria.mapper;

import com.serviteca.categoria.dto.CategoriaResponse;
import com.serviteca.categoria.entity.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    public CategoriaResponse toResponse(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNombre(),
                categoria.getDescripcion(), categoria.isActivo());
    }
}
