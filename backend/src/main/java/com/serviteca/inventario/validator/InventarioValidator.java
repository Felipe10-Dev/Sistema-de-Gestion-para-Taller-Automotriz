package com.serviteca.inventario.validator;

import com.serviteca.shared.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class InventarioValidator {

    public void validateMovimiento(String tipoMovimiento, Integer cantidadActual, Integer cantidad) {
        if (!tipoMovimiento.equals("ENTRADA") && !tipoMovimiento.equals("SALIDA")) {
            throw new BadRequestException("Tipo de movimiento inv\u00e1lido. Use ENTRADA o SALIDA");
        }

        if (tipoMovimiento.equals("SALIDA") && cantidadActual < cantidad) {
            throw new BadRequestException("Stock insuficiente. Stock actual: " + cantidadActual);
        }
    }
}
