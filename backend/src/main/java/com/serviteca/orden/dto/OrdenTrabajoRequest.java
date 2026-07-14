package com.serviteca.orden.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OrdenTrabajoRequest {

    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "El veh\u00edculo es obligatorio")
    private Long vehiculoId;

    private Integer kilometraje;
    private Long tecnicoId;
    private String observaciones;
    private List<ServicioItem> servicios;
    private List<ProductoItem> productos;

    public static class ServicioItem {
        private Long servicioId;
        private Integer cantidad;
        private String observaciones;

        public Long getServicioId() { return servicioId; }
        public void setServicioId(Long servicioId) { this.servicioId = servicioId; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        public String getObservaciones() { return observaciones; }
        public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    }

    public static class ProductoItem {
        private Long productoId;
        private Integer cantidad;

        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public Long getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(Long vehiculoId) { this.vehiculoId = vehiculoId; }
    public Integer getKilometraje() { return kilometraje; }
    public void setKilometraje(Integer kilometraje) { this.kilometraje = kilometraje; }
    public Long getTecnicoId() { return tecnicoId; }
    public void setTecnicoId(Long tecnicoId) { this.tecnicoId = tecnicoId; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public List<ServicioItem> getServicios() { return servicios; }
    public void setServicios(List<ServicioItem> servicios) { this.servicios = servicios; }
    public List<ProductoItem> getProductos() { return productos; }
    public void setProductos(List<ProductoItem> productos) { this.productos = productos; }
}
