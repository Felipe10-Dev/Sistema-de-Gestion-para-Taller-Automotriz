package com.serviteca.configuracion.dto;

import java.math.BigDecimal;

public class ImpuestoConfigResponse {
    private Long id;
    private String nombre;
    private BigDecimal porcentaje;
    private boolean activo;
    private boolean aplicacionPorDefecto;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getPorcentaje() { return porcentaje; }
    public void setPorcentaje(BigDecimal porcentaje) { this.porcentaje = porcentaje; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public boolean isAplicacionPorDefecto() { return aplicacionPorDefecto; }
    public void setAplicacionPorDefecto(boolean aplicacionPorDefecto) { this.aplicacionPorDefecto = aplicacionPorDefecto; }
}
