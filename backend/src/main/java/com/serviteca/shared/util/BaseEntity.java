package com.serviteca.shared.util;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @CreatedBy
    @Column(name = "creado_por", updatable = false)
    private String creadoPor;

    @LastModifiedBy
    @Column(name = "modificado_por")
    private String modificadoPor;

    @Column(name = "activo")
    private boolean activo = true;

    @Column(name = "fecha_eliminacion")
    private LocalDateTime fechaEliminacion;

    @Column(name = "eliminado_por")
    private String eliminadoPor;

    @Column(name = "motivo_eliminacion", length = 500)
    private String motivoEliminacion;

    @Column(name = "empresa_id")
    private Long empresaId;

    @Column(name = "sede_id")
    private Long sedeId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }
    public String getCreadoPor() { return creadoPor; }
    public void setCreadoPor(String creadoPor) { this.creadoPor = creadoPor; }
    public String getModificadoPor() { return modificadoPor; }
    public void setModificadoPor(String modificadoPor) { this.modificadoPor = modificadoPor; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public LocalDateTime getFechaEliminacion() { return fechaEliminacion; }
    public void setFechaEliminacion(LocalDateTime fechaEliminacion) { this.fechaEliminacion = fechaEliminacion; }
    public String getEliminadoPor() { return eliminadoPor; }
    public void setEliminadoPor(String eliminadoPor) { this.eliminadoPor = eliminadoPor; }
    public String getMotivoEliminacion() { return motivoEliminacion; }
    public void setMotivoEliminacion(String motivoEliminacion) { this.motivoEliminacion = motivoEliminacion; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }

    public void marcarComoEliminado(String username, String motivo) {
        this.activo = false;
        this.fechaEliminacion = LocalDateTime.now();
        this.eliminadoPor = username;
        this.motivoEliminacion = motivo;
    }

    public static void initTenant(BaseEntity entity) {
        if (entity.getEmpresaId() == null) {
            Long id = TenantContext.getEmpresaId();
            if (id == null) {
                try {
                    var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null && auth.isAuthenticated() && auth.getDetails() instanceof java.util.Map) {
                        Object v = ((java.util.Map<?, ?>) auth.getDetails()).get("empresaId");
                        if (v != null) id = ((Number) v).longValue();
                    }
                } catch (Exception ignored) {}
            }
            entity.setEmpresaId(id != null ? id : 1L);
        }
        if (entity.getSedeId() == null) {
            Long id = TenantContext.getSedeId();
            if (id == null) {
                try {
                    var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null && auth.isAuthenticated() && auth.getDetails() instanceof java.util.Map) {
                        Object v = ((java.util.Map<?, ?>) auth.getDetails()).get("sedeId");
                        if (v != null) id = ((Number) v).longValue();
                    }
                } catch (Exception ignored) {}
            }
            entity.setSedeId(id != null ? id : 1L);
        }
    }
}
