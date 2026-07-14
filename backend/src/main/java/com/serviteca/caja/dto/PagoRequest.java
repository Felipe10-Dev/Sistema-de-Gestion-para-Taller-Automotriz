package com.serviteca.caja.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class PagoRequest {

    @NotNull(message = "La orden es obligatoria")
    private Long ordenId;

    @NotNull(message = "El m\u00e9todo de pago es obligatorio")
    private Long metodoPagoId;

    @NotNull(message = "El valor es obligatorio")
    @Positive(message = "El valor debe ser mayor a cero")
    private BigDecimal valor;

    private String observacion;

    public Long getOrdenId() { return ordenId; }
    public void setOrdenId(Long ordenId) { this.ordenId = ordenId; }
    public Long getMetodoPagoId() { return metodoPagoId; }
    public void setMetodoPagoId(Long metodoPagoId) { this.metodoPagoId = metodoPagoId; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
