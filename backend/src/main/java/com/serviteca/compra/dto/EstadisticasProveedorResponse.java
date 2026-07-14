package com.serviteca.compra.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EstadisticasProveedorResponse {

    private Long proveedorId;
    private String proveedorNombre;
    private BigDecimal totalComprado;
    private long numeroOrdenes;
    private double promedioDiasEntrega;
    private LocalDateTime ultimaCompra;
    private long productosSuministrados;

    public Long getProveedorId() { return proveedorId; }
    public void setProveedorId(Long proveedorId) { this.proveedorId = proveedorId; }
    public String getProveedorNombre() { return proveedorNombre; }
    public void setProveedorNombre(String proveedorNombre) { this.proveedorNombre = proveedorNombre; }
    public BigDecimal getTotalComprado() { return totalComprado; }
    public void setTotalComprado(BigDecimal totalComprado) { this.totalComprado = totalComprado; }
    public long getNumeroOrdenes() { return numeroOrdenes; }
    public void setNumeroOrdenes(long numeroOrdenes) { this.numeroOrdenes = numeroOrdenes; }
    public double getPromedioDiasEntrega() { return promedioDiasEntrega; }
    public void setPromedioDiasEntrega(double promedioDiasEntrega) { this.promedioDiasEntrega = promedioDiasEntrega; }
    public LocalDateTime getUltimaCompra() { return ultimaCompra; }
    public void setUltimaCompra(LocalDateTime ultimaCompra) { this.ultimaCompra = ultimaCompra; }
    public long getProductosSuministrados() { return productosSuministrados; }
    public void setProductosSuministrados(long productosSuministrados) { this.productosSuministrados = productosSuministrados; }
}
