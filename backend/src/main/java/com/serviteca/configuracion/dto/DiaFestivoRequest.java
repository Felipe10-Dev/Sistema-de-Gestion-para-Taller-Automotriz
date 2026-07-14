package com.serviteca.configuracion.dto;

import java.time.LocalDate;

public class DiaFestivoRequest {
    private LocalDate fecha;
    private String descripcion;
    private Boolean activo;

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
