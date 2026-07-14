package com.serviteca.vehiculo.mantenimiento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MantenimientoRecomendacionRequest {

    @NotBlank(message = "El tipo es obligatorio")
    private String tipo;

    private String descripcion;

    @NotBlank(message = "El tipo de programaci\u00f3n es obligatorio")
    private String tipoProgramacion;

    private Integer intervaloKilometraje;
    private Integer intervaloDias;

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getTipoProgramacion() { return tipoProgramacion; }
    public void setTipoProgramacion(String tipoProgramacion) { this.tipoProgramacion = tipoProgramacion; }
    public Integer getIntervaloKilometraje() { return intervaloKilometraje; }
    public void setIntervaloKilometraje(Integer intervaloKilometraje) { this.intervaloKilometraje = intervaloKilometraje; }
    public Integer getIntervaloDias() { return intervaloDias; }
    public void setIntervaloDias(Integer intervaloDias) { this.intervaloDias = intervaloDias; }
}
