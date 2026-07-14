package com.serviteca.multiempresa.dto;

import java.util.List;

public class TransferenciaRequest {
    private Long sedeOrigenId;
    private Long sedeDestinoId;
    private String observaciones;
    private List<TransferenciaProductoItem> productos;

    public Long getSedeOrigenId() { return sedeOrigenId; }
    public void setSedeOrigenId(Long sedeOrigenId) { this.sedeOrigenId = sedeOrigenId; }
    public Long getSedeDestinoId() { return sedeDestinoId; }
    public void setSedeDestinoId(Long sedeDestinoId) { this.sedeDestinoId = sedeDestinoId; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public List<TransferenciaProductoItem> getProductos() { return productos; }
    public void setProductos(List<TransferenciaProductoItem> productos) { this.productos = productos; }

    public static class TransferenciaProductoItem {
        private Long productoId;
        private int cantidad;

        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }
        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    }
}
