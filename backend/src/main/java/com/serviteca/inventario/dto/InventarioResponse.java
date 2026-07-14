package com.serviteca.inventario.dto;

public class InventarioResponse {

    private Long id;
    private Long productoId;
    private String productoCodigo;
    private String productoNombre;
    private Integer cantidadActual;
    private Integer cantidadMinima;
    private boolean bajoStock;
    private String ubicacion;

    public InventarioResponse() {}

    public InventarioResponse(Long id, Long productoId, String productoCodigo, String productoNombre,
                              Integer cantidadActual, Integer cantidadMinima, boolean bajoStock, String ubicacion) {
        this.id = id;
        this.productoId = productoId;
        this.productoCodigo = productoCodigo;
        this.productoNombre = productoNombre;
        this.cantidadActual = cantidadActual;
        this.cantidadMinima = cantidadMinima;
        this.bajoStock = bajoStock;
        this.ubicacion = ubicacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public String getProductoCodigo() { return productoCodigo; }
    public void setProductoCodigo(String productoCodigo) { this.productoCodigo = productoCodigo; }
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    public Integer getCantidadActual() { return cantidadActual; }
    public void setCantidadActual(Integer cantidadActual) { this.cantidadActual = cantidadActual; }
    public Integer getCantidadMinima() { return cantidadMinima; }
    public void setCantidadMinima(Integer cantidadMinima) { this.cantidadMinima = cantidadMinima; }
    public boolean isBajoStock() { return bajoStock; }
    public void setBajoStock(boolean bajoStock) { this.bajoStock = bajoStock; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
}
