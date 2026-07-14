package com.serviteca.producto.mapper;

import com.serviteca.producto.dto.ProductoResponse;
import com.serviteca.producto.entity.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(
                producto.getId(), producto.getCodigo(), producto.getNombre(),
                producto.getDescripcion(), producto.getPrecioCompra(), producto.getPrecioVenta(),
                producto.getStockMinimo(), producto.getStockMaximo(), producto.getPuntoReposicion(),
                producto.getCategoria() != null ? producto.getCategoria().getId() : null,
                producto.getCategoria() != null ? producto.getCategoria().getNombre() : null,
                producto.getProveedor() != null ? producto.getProveedor().getId() : null,
                producto.getProveedor() != null ? producto.getProveedor().getNombre() : null,
                producto.isActivo()
        );
    }
}
