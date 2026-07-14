package com.serviteca.crm.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrdenClienteItem {

    private LocalDateTime fecha;
    private String vehiculoPlaca;
    private String vehiculoMarca;
    private String vehiculoLinea;
    private String numeroOrden;
    private String estado;
    private String estadoFinanciero;
    private BigDecimal total;
    private List<String> servicios;
    private List<String> productos;

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getVehiculoPlaca() { return vehiculoPlaca; }
    public void setVehiculoPlaca(String vehiculoPlaca) { this.vehiculoPlaca = vehiculoPlaca; }
    public String getVehiculoMarca() { return vehiculoMarca; }
    public void setVehiculoMarca(String vehiculoMarca) { this.vehiculoMarca = vehiculoMarca; }
    public String getVehiculoLinea() { return vehiculoLinea; }
    public void setVehiculoLinea(String vehiculoLinea) { this.vehiculoLinea = vehiculoLinea; }
    public String getNumeroOrden() { return numeroOrden; }
    public void setNumeroOrden(String numeroOrden) { this.numeroOrden = numeroOrden; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getEstadoFinanciero() { return estadoFinanciero; }
    public void setEstadoFinanciero(String estadoFinanciero) { this.estadoFinanciero = estadoFinanciero; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public List<String> getServicios() { return servicios; }
    public void setServicios(List<String> servicios) { this.servicios = servicios; }
    public List<String> getProductos() { return productos; }
    public void setProductos(List<String> productos) { this.productos = productos; }
}
