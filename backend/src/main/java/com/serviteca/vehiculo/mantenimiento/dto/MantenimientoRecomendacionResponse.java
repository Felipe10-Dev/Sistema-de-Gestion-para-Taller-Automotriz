package com.serviteca.vehiculo.mantenimiento.dto;

public class MantenimientoRecomendacionResponse {

    private Long id;
    private Long vehiculoId;
    private String vehiculoPlaca;
    private String tipo;
    private String descripcion;
    private String tipoProgramacion;
    private Integer intervaloKilometraje;
    private Integer intervaloDias;
    private boolean activo;

    public MantenimientoRecomendacionResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(Long vehiculoId) { this.vehiculoId = vehiculoId; }
    public String getVehiculoPlaca() { return vehiculoPlaca; }
    public void setVehiculoPlaca(String vehiculoPlaca) { this.vehiculoPlaca = vehiculoPlaca; }
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
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
