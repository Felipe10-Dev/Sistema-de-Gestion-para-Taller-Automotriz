package com.serviteca.caja.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "caja")
public class Caja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario", nullable = false, length = 50)
    private String usuario;

    @Column(name = "fecha_apertura", nullable = false)
    private LocalDateTime fechaApertura;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "monto_inicial", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoInicial = BigDecimal.ZERO;

    @Column(name = "total_ingresos", precision = 12, scale = 2)
    private BigDecimal totalIngresos = BigDecimal.ZERO;

    @Column(name = "total_egresos", precision = 12, scale = 2)
    private BigDecimal totalEgresos = BigDecimal.ZERO;

    @Column(name = "total_esperado", precision = 12, scale = 2)
    private BigDecimal totalEsperado = BigDecimal.ZERO;

    @Column(name = "monto_contado", precision = 12, scale = 2)
    private BigDecimal montoContado;

    @Column(name = "diferencia", precision = 12, scale = 2)
    private BigDecimal diferencia;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "empresa_id")
    private Long empresaId;

    @Column(name = "sede_id")
    private Long sedeId;

    @PrePersist
    protected void onCreate() {
        com.serviteca.shared.util.TenantUtil.setTenantFields(this);
        if (fechaApertura == null) {
            fechaApertura = LocalDateTime.now();
        }
        if (estado == null) {
            estado = "ABIERTA";
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public LocalDateTime getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(LocalDateTime fechaApertura) { this.fechaApertura = fechaApertura; }
    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }
    public BigDecimal getMontoInicial() { return montoInicial; }
    public void setMontoInicial(BigDecimal montoInicial) { this.montoInicial = montoInicial; }
    public BigDecimal getTotalIngresos() { return totalIngresos; }
    public void setTotalIngresos(BigDecimal totalIngresos) { this.totalIngresos = totalIngresos; }
    public BigDecimal getTotalEgresos() { return totalEgresos; }
    public void setTotalEgresos(BigDecimal totalEgresos) { this.totalEgresos = totalEgresos; }
    public BigDecimal getTotalEsperado() { return totalEsperado; }
    public void setTotalEsperado(BigDecimal totalEsperado) { this.totalEsperado = totalEsperado; }
    public BigDecimal getMontoContado() { return montoContado; }
    public void setMontoContado(BigDecimal montoContado) { this.montoContado = montoContado; }
    public BigDecimal getDiferencia() { return diferencia; }
    public void setDiferencia(BigDecimal diferencia) { this.diferencia = diferencia; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }
}
