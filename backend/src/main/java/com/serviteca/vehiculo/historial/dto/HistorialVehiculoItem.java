package com.serviteca.vehiculo.historial.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class HistorialVehiculoItem {

    private Long ordenId;
    private String numeroOrden;
    private LocalDateTime fecha;
    private String estado;
    private String estadoFinanciero;
    private List<String> serviciosRealizados;
    private List<String> productosUtilizados;
    private BigDecimal totalGeneral;
    private BigDecimal totalPagado;
    private String tecnicoNombre;
    private String observaciones;
    private Integer kilometraje;

    public Long getOrdenId() { return ordenId; }
    public void setOrdenId(Long ordenId) { this.ordenId = ordenId; }
    public String getNumeroOrden() { return numeroOrden; }
    public void setNumeroOrden(String numeroOrden) { this.numeroOrden = numeroOrden; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getEstadoFinanciero() { return estadoFinanciero; }
    public void setEstadoFinanciero(String estadoFinanciero) { this.estadoFinanciero = estadoFinanciero; }
    public List<String> getServiciosRealizados() { return serviciosRealizados; }
    public void setServiciosRealizados(List<String> serviciosRealizados) { this.serviciosRealizados = serviciosRealizados; }
    public List<String> getProductosUtilizados() { return productosUtilizados; }
    public void setProductosUtilizados(List<String> productosUtilizados) { this.productosUtilizados = productosUtilizados; }
    public BigDecimal getTotalGeneral() { return totalGeneral; }
    public void setTotalGeneral(BigDecimal totalGeneral) { this.totalGeneral = totalGeneral; }
    public BigDecimal getTotalPagado() { return totalPagado; }
    public void setTotalPagado(BigDecimal totalPagado) { this.totalPagado = totalPagado; }
    public String getTecnicoNombre() { return tecnicoNombre; }
    public void setTecnicoNombre(String tecnicoNombre) { this.tecnicoNombre = tecnicoNombre; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public Integer getKilometraje() { return kilometraje; }
    public void setKilometraje(Integer kilometraje) { this.kilometraje = kilometraje; }
}
