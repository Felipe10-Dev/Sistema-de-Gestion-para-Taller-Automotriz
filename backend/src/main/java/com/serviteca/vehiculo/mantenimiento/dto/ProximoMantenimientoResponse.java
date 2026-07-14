package com.serviteca.vehiculo.mantenimiento.dto;

import java.time.LocalDate;

public class ProximoMantenimientoResponse {

    private Long recomendacionId;
    private String tipo;
    private String descripcion;
    private Integer ultimoKilometraje;
    private Integer proximoKilometraje;
    private LocalDate ultimaFecha;
    private LocalDate proximaFecha;
    private Integer kilometrosRestantes;
    private Long diasRestantes;
    private String alerta;

    public Long getRecomendacionId() { return recomendacionId; }
    public void setRecomendacionId(Long recomendacionId) { this.recomendacionId = recomendacionId; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getUltimoKilometraje() { return ultimoKilometraje; }
    public void setUltimoKilometraje(Integer ultimoKilometraje) { this.ultimoKilometraje = ultimoKilometraje; }
    public Integer getProximoKilometraje() { return proximoKilometraje; }
    public void setProximoKilometraje(Integer proximoKilometraje) { this.proximoKilometraje = proximoKilometraje; }
    public LocalDate getUltimaFecha() { return ultimaFecha; }
    public void setUltimaFecha(LocalDate ultimaFecha) { this.ultimaFecha = ultimaFecha; }
    public LocalDate getProximaFecha() { return proximaFecha; }
    public void setProximaFecha(LocalDate proximaFecha) { this.proximaFecha = proximaFecha; }
    public Integer getKilometrosRestantes() { return kilometrosRestantes; }
    public void setKilometrosRestantes(Integer kilometrosRestantes) { this.kilometrosRestantes = kilometrosRestantes; }
    public Long getDiasRestantes() { return diasRestantes; }
    public void setDiasRestantes(Long diasRestantes) { this.diasRestantes = diasRestantes; }
    public String getAlerta() { return alerta; }
    public void setAlerta(String alerta) { this.alerta = alerta; }
}
