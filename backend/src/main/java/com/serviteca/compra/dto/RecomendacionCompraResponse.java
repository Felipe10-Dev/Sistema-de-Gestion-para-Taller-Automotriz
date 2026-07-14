package com.serviteca.compra.dto;

import java.math.BigDecimal;

public class RecomendacionCompraResponse {

    private Long productoId;
    private String productoCodigo;
    private String productoNombre;
    private Integer stockActual;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private Integer puntoReposicion;
    private Integer cantidadSugerida;
    private String ultimoProveedor;
    private BigDecimal ultimoPrecioCompra;
    private String alerta;

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public String getProductoCodigo() { return productoCodigo; }
    public void setProductoCodigo(String productoCodigo) { this.productoCodigo = productoCodigo; }
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    public Integer getStockMaximo() { return stockMaximo; }
    public void setStockMaximo(Integer stockMaximo) { this.stockMaximo = stockMaximo; }
    public Integer getPuntoReposicion() { return puntoReposicion; }
    public void setPuntoReposicion(Integer puntoReposicion) { this.puntoReposicion = puntoReposicion; }
    public Integer getCantidadSugerida() { return cantidadSugerida; }
    public void setCantidadSugerida(Integer cantidadSugerida) { this.cantidadSugerida = cantidadSugerida; }
    public String getUltimoProveedor() { return ultimoProveedor; }
    public void setUltimoProveedor(String ultimoProveedor) { this.ultimoProveedor = ultimoProveedor; }
    public BigDecimal getUltimoPrecioCompra() { return ultimoPrecioCompra; }
    public void setUltimoPrecioCompra(BigDecimal ultimoPrecioCompra) { this.ultimoPrecioCompra = ultimoPrecioCompra; }
    public String getAlerta() { return alerta; }
    public void setAlerta(String alerta) { this.alerta = alerta; }
}
