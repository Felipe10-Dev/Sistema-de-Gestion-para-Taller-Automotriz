package com.serviteca.configuracion.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "configuracion_impuestos")
public class ImpuestoConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentaje;

    @Column(name = "activo")
    private boolean activo = true;

    @Column(name = "aplicacion_por_defecto")
    private boolean aplicacionPorDefecto = false;

    @Column(name = "empresa_id")
    private Long empresaId;

    @Column(name = "sede_id")
    private Long sedeId;

    @PrePersist
    protected void onCreate() { com.serviteca.shared.util.TenantUtil.setTenantFields(this); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getPorcentaje() { return porcentaje; }
    public void setPorcentaje(BigDecimal porcentaje) { this.porcentaje = porcentaje; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public boolean isAplicacionPorDefecto() { return aplicacionPorDefecto; }
    public void setAplicacionPorDefecto(boolean aplicacionPorDefecto) { this.aplicacionPorDefecto = aplicacionPorDefecto; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }
}
