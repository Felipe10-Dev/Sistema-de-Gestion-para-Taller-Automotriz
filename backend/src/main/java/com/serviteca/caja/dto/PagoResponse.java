package com.serviteca.caja.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagoResponse {

    private Long id;
    private Long ordenId;
    private String ordenNumero;
    private String usuario;
    private LocalDateTime fecha;
    private Long metodoPagoId;
    private String metodoPagoNombre;
    private BigDecimal valor;
    private String observacion;
    private boolean pagoAnulado;
    private Long pagoOriginalId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrdenId() { return ordenId; }
    public void setOrdenId(Long ordenId) { this.ordenId = ordenId; }
    public String getOrdenNumero() { return ordenNumero; }
    public void setOrdenNumero(String ordenNumero) { this.ordenNumero = ordenNumero; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public Long getMetodoPagoId() { return metodoPagoId; }
    public void setMetodoPagoId(Long metodoPagoId) { this.metodoPagoId = metodoPagoId; }
    public String getMetodoPagoNombre() { return metodoPagoNombre; }
    public void setMetodoPagoNombre(String metodoPagoNombre) { this.metodoPagoNombre = metodoPagoNombre; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public boolean isPagoAnulado() { return pagoAnulado; }
    public void setPagoAnulado(boolean pagoAnulado) { this.pagoAnulado = pagoAnulado; }
    public Long getPagoOriginalId() { return pagoOriginalId; }
    public void setPagoOriginalId(Long pagoOriginalId) { this.pagoOriginalId = pagoOriginalId; }
}
