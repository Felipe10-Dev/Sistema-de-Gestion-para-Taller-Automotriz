package com.serviteca.orden.dto;

import jakarta.validation.constraints.NotBlank;

public class CambioEstadoRequest {

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    private String observaciones;

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
