package com.serviteca.configuracion.dto;

public class PermisoModuloRequest {
    private Long rolId;
    private String modulo;
    private String permiso;
    private Boolean activo;

    public Long getRolId() { return rolId; }
    public void setRolId(Long rolId) { this.rolId = rolId; }
    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }
    public String getPermiso() { return permiso; }
    public void setPermiso(String permiso) { this.permiso = permiso; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
