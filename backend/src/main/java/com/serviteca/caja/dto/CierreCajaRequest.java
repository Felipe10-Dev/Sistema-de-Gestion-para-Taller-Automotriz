package com.serviteca.caja.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class CierreCajaRequest {

    @NotNull(message = "El monto contado es obligatorio")
    @PositiveOrZero(message = "El monto contado debe ser mayor o igual a cero")
    private BigDecimal montoContado;

    private String observaciones;

    public BigDecimal getMontoContado() { return montoContado; }
    public void setMontoContado(BigDecimal montoContado) { this.montoContado = montoContado; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
