package com.serviteca.vehiculo.historial.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EventoLineaTiempo {

    private LocalDateTime fecha;
    private String tipo;
    private String descripcion;
    private Integer kilometraje;
    private BigDecimal totalOrden;
    private String observaciones;

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getKilometraje() { return kilometraje; }
    public void setKilometraje(Integer kilometraje) { this.kilometraje = kilometraje; }
    public BigDecimal getTotalOrden() { return totalOrden; }
    public void setTotalOrden(BigDecimal totalOrden) { this.totalOrden = totalOrden; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
