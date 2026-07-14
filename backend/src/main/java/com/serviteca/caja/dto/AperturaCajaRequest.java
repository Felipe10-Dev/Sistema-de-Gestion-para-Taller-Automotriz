package com.serviteca.caja.dto;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class AperturaCajaRequest {

    @Positive(message = "El monto inicial debe ser mayor o igual a cero")
    private BigDecimal montoInicial = BigDecimal.ZERO;

    private String observacion;

    public BigDecimal getMontoInicial() { return montoInicial; }
    public void setMontoInicial(BigDecimal montoInicial) { this.montoInicial = montoInicial; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
