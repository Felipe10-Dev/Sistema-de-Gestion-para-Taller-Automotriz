package com.serviteca.vehiculo.mantenimiento.entity;

import com.serviteca.vehiculo.entity.Vehiculo;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mantenimiento_recomendaciones")
public class MantenimientoRecomendacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @Column(name = "tipo", nullable = false, length = 30)
    private String tipo;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "tipo_programacion", nullable = false, length = 20)
    private String tipoProgramacion;

    @Column(name = "intervalo_kilometraje")
    private Integer intervaloKilometraje;

    @Column(name = "intervalo_dias")
    private Integer intervaloDias;

    @Column(name = "activo")
    private boolean activo = true;

    @Column(name = "fecha_eliminacion")
    private LocalDateTime fechaEliminacion;

    @Column(name = "eliminado_por", length = 100)
    private String eliminadoPor;

    @Column(name = "motivo_eliminacion", length = 500)
    private String motivoEliminacion;

    @Column(name = "empresa_id")
    private Long empresaId;

    @Column(name = "sede_id")
    private Long sedeId;

    @PrePersist
    protected void onCreate() {
        com.serviteca.shared.util.TenantUtil.setTenantFields(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getTipoProgramacion() { return tipoProgramacion; }
    public void setTipoProgramacion(String tipoProgramacion) { this.tipoProgramacion = tipoProgramacion; }
    public Integer getIntervaloKilometraje() { return intervaloKilometraje; }
    public void setIntervaloKilometraje(Integer intervaloKilometraje) { this.intervaloKilometraje = intervaloKilometraje; }
    public Integer getIntervaloDias() { return intervaloDias; }
    public void setIntervaloDias(Integer intervaloDias) { this.intervaloDias = intervaloDias; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }
    public LocalDateTime getFechaEliminacion() { return fechaEliminacion; }
    public void setFechaEliminacion(LocalDateTime fechaEliminacion) { this.fechaEliminacion = fechaEliminacion; }
    public String getEliminadoPor() { return eliminadoPor; }
    public void setEliminadoPor(String eliminadoPor) { this.eliminadoPor = eliminadoPor; }
    public String getMotivoEliminacion() { return motivoEliminacion; }
    public void setMotivoEliminacion(String motivoEliminacion) { this.motivoEliminacion = motivoEliminacion; }
}
