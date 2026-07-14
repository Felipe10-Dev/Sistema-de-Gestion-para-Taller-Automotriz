package com.serviteca.inventario.dto;

import jakarta.validation.constraints.NotNull;

public class InventarioRequest {

    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    private Integer cantidadMinima = 0;
    private String ubicacion;

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public Integer getCantidadMinima() { return cantidadMinima; }
    public void setCantidadMinima(Integer cantidadMinima) { this.cantidadMinima = cantidadMinima; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
}
