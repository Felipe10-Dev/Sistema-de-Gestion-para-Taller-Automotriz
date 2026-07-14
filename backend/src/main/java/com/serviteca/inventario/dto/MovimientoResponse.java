package com.serviteca.inventario.dto;

import java.time.LocalDateTime;

public class MovimientoResponse {

    private Long id;
    private Long productoId;
    private String productoNombre;
    private String tipoMovimiento;
    private Integer cantidad;
    private String motivo;
    private LocalDateTime fechaMovimiento;
    private String usuario;

    public MovimientoResponse() {}

    public MovimientoResponse(Long id, Long productoId, String productoNombre, String tipoMovimiento,
                              Integer cantidad, String motivo, LocalDateTime fechaMovimiento, String usuario) {
        this.id = id;
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.motivo = motivo;
        this.fechaMovimiento = fechaMovimiento;
        this.usuario = usuario;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public LocalDateTime getFechaMovimiento() { return fechaMovimiento; }
    public void setFechaMovimiento(LocalDateTime fechaMovimiento) { this.fechaMovimiento = fechaMovimiento; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
}
