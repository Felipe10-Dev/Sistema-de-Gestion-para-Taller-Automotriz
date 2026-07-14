package com.serviteca.producto.dto;

import java.math.BigDecimal;

public class ProductoResponse {

    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private Integer puntoReposicion;
    private Long categoriaId;
    private String categoriaNombre;
    private Long proveedorId;
    private String proveedorNombre;
    private boolean activo;

    public ProductoResponse() {}

    public ProductoResponse(Long id, String codigo, String nombre, String descripcion,
                            BigDecimal precioCompra, BigDecimal precioVenta,
                            Integer stockMinimo, Integer stockMaximo, Integer puntoReposicion,
                            Long categoriaId, String categoriaNombre, Long proveedorId,
                            String proveedorNombre, boolean activo) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stockMinimo = stockMinimo;
        this.stockMaximo = stockMaximo;
        this.puntoReposicion = puntoReposicion;
        this.categoriaId = categoriaId;
        this.categoriaNombre = categoriaNombre;
        this.proveedorId = proveedorId;
        this.proveedorNombre = proveedorNombre;
        this.activo = activo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(BigDecimal precioCompra) { this.precioCompra = precioCompra; }
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    public Integer getStockMaximo() { return stockMaximo; }
    public void setStockMaximo(Integer stockMaximo) { this.stockMaximo = stockMaximo; }
    public Integer getPuntoReposicion() { return puntoReposicion; }
    public void setPuntoReposicion(Integer puntoReposicion) { this.puntoReposicion = puntoReposicion; }
    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }
    public Long getProveedorId() { return proveedorId; }
    public void setProveedorId(Long proveedorId) { this.proveedorId = proveedorId; }
    public String getProveedorNombre() { return proveedorNombre; }
    public void setProveedorNombre(String proveedorNombre) { this.proveedorNombre = proveedorNombre; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
