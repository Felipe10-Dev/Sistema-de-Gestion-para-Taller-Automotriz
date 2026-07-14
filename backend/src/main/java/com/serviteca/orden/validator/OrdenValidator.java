package com.serviteca.orden.validator;

import com.serviteca.orden.entity.OrdenTrabajo;
import com.serviteca.orden.enums.OrdenEstado;
import com.serviteca.orden.repository.OrdenProductoRepository;
import com.serviteca.orden.repository.OrdenServicioRepository;
import com.serviteca.shared.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class OrdenValidator {

    private final OrdenServicioRepository ordenServicioRepository;
    private final OrdenProductoRepository ordenProductoRepository;

    public OrdenValidator(OrdenServicioRepository ordenServicioRepository,
                          OrdenProductoRepository ordenProductoRepository) {
        this.ordenServicioRepository = ordenServicioRepository;
        this.ordenProductoRepository = ordenProductoRepository;
    }

    public void validarEstado(String estado) {
        try {
            OrdenEstado.valueOf(estado);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Estado inv\u00e1lido: " + estado +
                    ". Estados v\u00e1lidos: PENDIENTE, EN_DIAGNOSTICO, EN_PROCESO, " +
                    "ESPERANDO_REPUESTOS, LISTO_PARA_ENTREGA, ENTREGADO, CANCELADO");
        }
    }

    public void validarTransicion(String estadoActual, String nuevoEstado) {
        try {
            OrdenEstado actual = OrdenEstado.valueOf(estadoActual);
            OrdenEstado destino = OrdenEstado.valueOf(nuevoEstado);
            if (!actual.puedeTransicionarA(destino)) {
                throw new BadRequestException(
                        "No se puede cambiar de " + estadoActual + " a " + nuevoEstado);
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Estado inv\u00e1lido: " + e.getMessage());
        }
    }

    public void validarTerminal(String estado) {
        try {
            OrdenEstado actual = OrdenEstado.valueOf(estado);
            if (actual.isTerminal()) {
                throw new BadRequestException(
                        "No se puede modificar una orden en estado " + estado);
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Estado inv\u00e1lido: " + e.getMessage());
        }
    }

    public void validarParaEntrega(OrdenTrabajo orden) {
        if (orden.getCliente() == null) {
            throw new BadRequestException("La orden debe tener un cliente asignado");
        }
        if (orden.getVehiculo() == null) {
            throw new BadRequestException("La orden debe tener un veh\u00edculo asociado");
        }
        long servicios = ordenServicioRepository.findByOrdenId(orden.getId()).size();
        long productos = ordenProductoRepository.findByOrdenId(orden.getId()).size();
        if (servicios == 0 && productos == 0) {
            throw new BadRequestException(
                    "La orden debe tener al menos un servicio o un producto para ser entregada");
        }
    }
}
