package com.serviteca.configuracion.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "configuracion_numeracion")
public class NumeracionConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String modulo;

    @Column(length = 10)
    private String prefijo = "";

    @Column(length = 10)
    private String sufijo = "";

    @Column(nullable = false)
    private int longitud = 8;

    @Column(name = "numero_actual", nullable = false)
    private int numeroActual = 0;

    @Column(name = "numero_inicial", nullable = false)
    private int numeroInicial = 1;

    @Column(name = "reinicio_anual")
    private boolean reinicioAnual = false;

    @Column(name = "activo")
    private boolean activo = true;

    @Column(name = "empresa_id")
    private Long empresaId;

    @Column(name = "sede_id")
    private Long sedeId;

    @PrePersist
    protected void onCreate() { com.serviteca.shared.util.TenantUtil.setTenantFields(this); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }
    public String getPrefijo() { return prefijo; }
    public void setPrefijo(String prefijo) { this.prefijo = prefijo; }
    public String getSufijo() { return sufijo; }
    public void setSufijo(String sufijo) { this.sufijo = sufijo; }
    public int getLongitud() { return longitud; }
    public void setLongitud(int longitud) { this.longitud = longitud; }
    public int getNumeroActual() { return numeroActual; }
    public void setNumeroActual(int numeroActual) { this.numeroActual = numeroActual; }
    public int getNumeroInicial() { return numeroInicial; }
    public void setNumeroInicial(int numeroInicial) { this.numeroInicial = numeroInicial; }
    public boolean isReinicioAnual() { return reinicioAnual; }
    public void setReinicioAnual(boolean reinicioAnual) { this.reinicioAnual = reinicioAnual; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }
}
