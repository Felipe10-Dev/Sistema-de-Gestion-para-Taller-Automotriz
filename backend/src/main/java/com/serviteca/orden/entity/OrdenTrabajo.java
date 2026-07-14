package com.serviteca.orden.entity;

import com.serviteca.cliente.entity.Cliente;
import com.serviteca.shared.util.BaseEntity;
import com.serviteca.usuario.entity.Usuario;
import com.serviteca.vehiculo.entity.Vehiculo;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ordenes_trabajo")
public class OrdenTrabajo extends BaseEntity {

    @Column(name = "numero_orden", unique = true, nullable = false, length = 20)
    private String numeroOrden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @Column(name = "kilometraje")
    private Integer kilometraje;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @Column(name = "estado_financiero", nullable = false, length = 20)
    private String estadoFinanciero;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_id")
    private Usuario tecnico;

    @Column(name = "observaciones", length = 1000)
    private String observaciones;

    @Column(name = "total_servicios", precision = 10, scale = 2)
    private BigDecimal totalServicios = BigDecimal.ZERO;

    @Column(name = "total_productos", precision = 10, scale = 2)
    private BigDecimal totalProductos = BigDecimal.ZERO;

    @Column(name = "total_general", precision = 10, scale = 2)
    private BigDecimal totalGeneral = BigDecimal.ZERO;

    @PrePersist
    protected void onCreate() {
        com.serviteca.shared.util.TenantUtil.setTenantFields(this);
        fechaIngreso = LocalDateTime.now();
        if (estado == null) {
            estado = "PENDIENTE";
        }
        if (estadoFinanciero == null) {
            estadoFinanciero = "SIN_PAGAR";
        }
    }

    public String getNumeroOrden() { return numeroOrden; }
    public void setNumeroOrden(String numeroOrden) { this.numeroOrden = numeroOrden; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }
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
    public Usuario getTecnico() { return tecnico; }
    public void setTecnico(Usuario tecnico) { this.tecnico = tecnico; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public BigDecimal getTotalServicios() { return totalServicios; }
    public void setTotalServicios(BigDecimal totalServicios) { this.totalServicios = totalServicios; }
    public BigDecimal getTotalProductos() { return totalProductos; }
    public void setTotalProductos(BigDecimal totalProductos) { this.totalProductos = totalProductos; }
    public BigDecimal getTotalGeneral() { return totalGeneral; }
    public void setTotalGeneral(BigDecimal totalGeneral) { this.totalGeneral = totalGeneral; }
}
