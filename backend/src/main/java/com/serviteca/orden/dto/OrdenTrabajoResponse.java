package com.serviteca.orden.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrdenTrabajoResponse {

    private Long id;
    private String numeroOrden;
    private Long clienteId;
    private String clienteNombre;
    private String clienteDocumento;
    private Long vehiculoId;
    private String vehiculoPlaca;
    private String vehiculoMarca;
    private String vehiculoLinea;
    private Integer kilometraje;
    private String estado;
    private String estadoFinanciero;
    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaSalida;
    private Long tecnicoId;
    private String tecnicoNombre;
    private String observaciones;
    private BigDecimal totalServicios;
    private BigDecimal totalProductos;
    private BigDecimal totalGeneral;
    private List<ServicioResponse> servicios;
    private List<ProductoResponse> productos;
    private List<HistorialResponse> historial;
    private List<ObservacionResponse> observacionesList;

    public static class ServicioResponse {
        private Long id;
        private Long servicioId;
        private String servicioNombre;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
        private String observaciones;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getServicioId() { return servicioId; }
        public void setServicioId(Long servicioId) { this.servicioId = servicioId; }
        public String getServicioNombre() { return servicioNombre; }
        public void setServicioNombre(String servicioNombre) { this.servicioNombre = servicioNombre; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        public BigDecimal getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
        public String getObservaciones() { return observaciones; }
        public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    }

    public static class ProductoResponse {
        private Long id;
        private Long productoId;
        private String productoNombre;
        private String productoCodigo;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }
        public String getProductoNombre() { return productoNombre; }
        public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
        public String getProductoCodigo() { return productoCodigo; }
        public void setProductoCodigo(String productoCodigo) { this.productoCodigo = productoCodigo; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        public BigDecimal getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    }

    public static class HistorialResponse {
        private Long id;
        private String estadoAnterior;
        private String estadoNuevo;
        private String usuario;
        private LocalDateTime fecha;
        private String observacion;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getEstadoAnterior() { return estadoAnterior; }
        public void setEstadoAnterior(String estadoAnterior) { this.estadoAnterior = estadoAnterior; }
        public String getEstadoNuevo() { return estadoNuevo; }
        public void setEstadoNuevo(String estadoNuevo) { this.estadoNuevo = estadoNuevo; }
        public String getUsuario() { return usuario; }
        public void setUsuario(String usuario) { this.usuario = usuario; }
        public LocalDateTime getFecha() { return fecha; }
        public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
        public String getObservacion() { return observacion; }
        public void setObservacion(String observacion) { this.observacion = observacion; }
    }

    public static class ObservacionResponse {
        private Long id;
        private String usuario;
        private LocalDateTime fecha;
        private String comentario;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsuario() { return usuario; }
        public void setUsuario(String usuario) { this.usuario = usuario; }
        public LocalDateTime getFecha() { return fecha; }
        public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
        public String getComentario() { return comentario; }
        public void setComentario(String comentario) { this.comentario = comentario; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumeroOrden() { return numeroOrden; }
    public void setNumeroOrden(String numeroOrden) { this.numeroOrden = numeroOrden; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public String getClienteDocumento() { return clienteDocumento; }
    public void setClienteDocumento(String clienteDocumento) { this.clienteDocumento = clienteDocumento; }
    public Long getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(Long vehiculoId) { this.vehiculoId = vehiculoId; }
    public String getVehiculoPlaca() { return vehiculoPlaca; }
    public void setVehiculoPlaca(String vehiculoPlaca) { this.vehiculoPlaca = vehiculoPlaca; }
    public String getVehiculoMarca() { return vehiculoMarca; }
    public void setVehiculoMarca(String vehiculoMarca) { this.vehiculoMarca = vehiculoMarca; }
    public String getVehiculoLinea() { return vehiculoLinea; }
    public void setVehiculoLinea(String vehiculoLinea) { this.vehiculoLinea = vehiculoLinea; }
    public Integer getKilometraje() { return kilometraje; }
    public void setKilometraje(Integer kilometraje) { this.kilometraje = kilometraje; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getEstadoFinanciero() { return estadoFinanciero; }
    public void setEstadoFinanciero(String estadoFinanciero) { this.estadoFinanciero = estadoFinanciero; }
    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDateTime fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public LocalDateTime getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDateTime fechaSalida) { this.fechaSalida = fechaSalida; }
    public Long getTecnicoId() { return tecnicoId; }
    public void setTecnicoId(Long tecnicoId) { this.tecnicoId = tecnicoId; }
    public String getTecnicoNombre() { return tecnicoNombre; }
    public void setTecnicoNombre(String tecnicoNombre) { this.tecnicoNombre = tecnicoNombre; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public BigDecimal getTotalServicios() { return totalServicios; }
    public void setTotalServicios(BigDecimal totalServicios) { this.totalServicios = totalServicios; }
    public BigDecimal getTotalProductos() { return totalProductos; }
    public void setTotalProductos(BigDecimal totalProductos) { this.totalProductos = totalProductos; }
    public BigDecimal getTotalGeneral() { return totalGeneral; }
    public void setTotalGeneral(BigDecimal totalGeneral) { this.totalGeneral = totalGeneral; }
    public List<ServicioResponse> getServicios() { return servicios; }
    public void setServicios(List<ServicioResponse> servicios) { this.servicios = servicios; }
    public List<ProductoResponse> getProductos() { return productos; }
    public void setProductos(List<ProductoResponse> productos) { this.productos = productos; }
    public List<HistorialResponse> getHistorial() { return historial; }
    public void setHistorial(List<HistorialResponse> historial) { this.historial = historial; }
    public List<ObservacionResponse> getObservacionesList() { return observacionesList; }
    public void setObservacionesList(List<ObservacionResponse> observacionesList) { this.observacionesList = observacionesList; }
}
