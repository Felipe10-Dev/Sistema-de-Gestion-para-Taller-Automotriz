package com.serviteca.configuracion.dto;

import java.math.BigDecimal;

public class ImpuestoConfigRequest {
    private String nombre;
    private BigDecimal porcentaje;
    private Boolean activo;
    private boolean aplicacionPorDefecto;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getPorcentaje() { return porcentaje; }
    public void setPorcentaje(BigDecimal porcentaje) { this.porcentaje = porcentaje; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public boolean isAplicacionPorDefecto() { return aplicacionPorDefecto; }
    public void setAplicacionPorDefecto(boolean aplicacionPorDefecto) { this.aplicacionPorDefecto = aplicacionPorDefecto; }
}
