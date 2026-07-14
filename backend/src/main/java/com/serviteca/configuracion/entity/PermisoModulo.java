package com.serviteca.configuracion.entity;

import com.serviteca.rol.entity.Rol;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "permisos_modulo")
public class PermisoModulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @Column(nullable = false, length = 50)
    private String modulo;

    @Column(nullable = false, length = 20)
    private String permiso;

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
    protected void onCreate() { com.serviteca.shared.util.TenantUtil.setTenantFields(this); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }
    public String getPermiso() { return permiso; }
    public void setPermiso(String permiso) { this.permiso = permiso; }
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
