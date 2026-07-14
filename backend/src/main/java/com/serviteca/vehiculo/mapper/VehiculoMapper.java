package com.serviteca.vehiculo.mapper;

import com.serviteca.vehiculo.dto.VehiculoResponse;
import com.serviteca.vehiculo.entity.Vehiculo;
import org.springframework.stereotype.Component;

@Component
public class VehiculoMapper {

    public VehiculoResponse toResponse(Vehiculo vehiculo) {
        return new VehiculoResponse(
                vehiculo.getId(),
                vehiculo.getPlaca(),
                vehiculo.getMarca(),
                vehiculo.getLinea(),
                vehiculo.getModelo(),
                vehiculo.getColor(),
                vehiculo.getCilindraje(),
                vehiculo.getTipoVehiculo(),
                vehiculo.getMotor(),
                vehiculo.getCombustible(),
                vehiculo.getVin(),
                vehiculo.getAnio(),
                vehiculo.getCliente().getId(),
                vehiculo.getCliente().getNombre() + " " + vehiculo.getCliente().getApellido(),
                vehiculo.isActivo()
        );
    }
}
