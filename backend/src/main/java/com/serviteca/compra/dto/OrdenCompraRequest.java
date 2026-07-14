package com.serviteca.compra.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class OrdenCompraRequest {

    private Long proveedorId;
    private String observaciones;

    @NotEmpty(message = "Debe incluir al menos un producto")
    @Valid
    private List<ProductoCompraItem> productos;

    public Long getProveedorId() { return proveedorId; }
    public void setProveedorId(Long proveedorId) { this.proveedorId = proveedorId; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public List<ProductoCompraItem> getProductos() { return productos; }
    public void setProductos(List<ProductoCompraItem> productos) { this.productos = productos; }

    public static class ProductoCompraItem {
        private Long productoId;
        private Integer cantidad;
        private java.math.BigDecimal precioUnitario;

        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        public java.math.BigDecimal getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(java.math.BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    }
}
