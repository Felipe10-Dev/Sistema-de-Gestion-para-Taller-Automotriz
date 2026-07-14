package com.serviteca.inventario.mapper;

import com.serviteca.inventario.dto.InventarioResponse;
import com.serviteca.inventario.dto.MovimientoResponse;
import com.serviteca.inventario.entity.Inventario;
import com.serviteca.inventario.entity.MovimientoInventario;
import org.springframework.stereotype.Component;

@Component
public class InventarioMapper {

    public InventarioResponse toResponse(Inventario inventario) {
        return new InventarioResponse(
                inventario.getId(),
                inventario.getProducto().getId(),
                inventario.getProducto().getCodigo(),
                inventario.getProducto().getNombre(),
                inventario.getCantidadActual(),
                inventario.getCantidadMinima(),
                inventario.getCantidadActual() <= inventario.getCantidadMinima(),
                inventario.getUbicacion()
        );
    }

    public MovimientoResponse toMovimientoResponse(MovimientoInventario movimiento) {
        return new MovimientoResponse(
                movimiento.getId(),
                movimiento.getProducto().getId(),
                movimiento.getProducto().getNombre(),
                movimiento.getTipoMovimiento(),
                movimiento.getCantidad(),
                movimiento.getMotivo(),
                movimiento.getFechaMovimiento(),
                movimiento.getUsuario()
        );
    }
}
