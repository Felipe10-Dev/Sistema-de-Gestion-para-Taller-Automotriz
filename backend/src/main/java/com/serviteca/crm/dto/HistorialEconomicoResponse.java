package com.serviteca.crm.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HistorialEconomicoResponse {

    private BigDecimal totalInvertido;
    private long cantidadOrdenes;
    private BigDecimal promedioPorOrden;
    private BigDecimal mayorCompra;
    private LocalDateTime ultimaCompra;
    private BigDecimal totalPendientePago;
    private BigDecimal totalCancelado;

    public BigDecimal getTotalInvertido() { return totalInvertido; }
    public void setTotalInvertido(BigDecimal totalInvertido) { this.totalInvertido = totalInvertido; }
    public long getCantidadOrdenes() { return cantidadOrdenes; }
    public void setCantidadOrdenes(long cantidadOrdenes) { this.cantidadOrdenes = cantidadOrdenes; }
    public BigDecimal getPromedioPorOrden() { return promedioPorOrden; }
    public void setPromedioPorOrden(BigDecimal promedioPorOrden) { this.promedioPorOrden = promedioPorOrden; }
    public BigDecimal getMayorCompra() { return mayorCompra; }
    public void setMayorCompra(BigDecimal mayorCompra) { this.mayorCompra = mayorCompra; }
    public LocalDateTime getUltimaCompra() { return ultimaCompra; }
    public void setUltimaCompra(LocalDateTime ultimaCompra) { this.ultimaCompra = ultimaCompra; }
    public BigDecimal getTotalPendientePago() { return totalPendientePago; }
    public void setTotalPendientePago(BigDecimal totalPendientePago) { this.totalPendientePago = totalPendientePago; }
    public BigDecimal getTotalCancelado() { return totalCancelado; }
    public void setTotalCancelado(BigDecimal totalCancelado) { this.totalCancelado = totalCancelado; }
}
