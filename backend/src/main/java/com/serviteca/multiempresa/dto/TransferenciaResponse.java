package com.serviteca.multiempresa.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TransferenciaResponse {
    private Long id;
    private Long empresaId;
    private Long sedeOrigenId;
    private String sedeOrigenNombre;
    private Long sedeDestinoId;
    private String sedeDestinoNombre;
    private LocalDateTime fecha;
    private String usuario;
    private String observaciones;
    private List<ProductoTransferido> productos;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
    public Long getSedeOrigenId() { return sedeOrigenId; }
    public void setSedeOrigenId(Long sedeOrigenId) { this.sedeOrigenId = sedeOrigenId; }
    public String getSedeOrigenNombre() { return sedeOrigenNombre; }
    public void setSedeOrigenNombre(String sedeOrigenNombre) { this.sedeOrigenNombre = sedeOrigenNombre; }
    public Long getSedeDestinoId() { return sedeDestinoId; }
    public void setSedeDestinoId(Long sedeDestinoId) { this.sedeDestinoId = sedeDestinoId; }
    public String getSedeDestinoNombre() { return sedeDestinoNombre; }
    public void setSedeDestinoNombre(String sedeDestinoNombre) { this.sedeDestinoNombre = sedeDestinoNombre; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public List<ProductoTransferido> getProductos() { return productos; }
    public void setProductos(List<ProductoTransferido> productos) { this.productos = productos; }

    public static class ProductoTransferido {
        private Long productoId;
        private String productoNombre;
        private String productoCodigo;
        private int cantidad;

        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }
        public String getProductoNombre() { return productoNombre; }
        public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
        public String getProductoCodigo() { return productoCodigo; }
        public void setProductoCodigo(String productoCodigo) { this.productoCodigo = productoCodigo; }
        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    }
}
