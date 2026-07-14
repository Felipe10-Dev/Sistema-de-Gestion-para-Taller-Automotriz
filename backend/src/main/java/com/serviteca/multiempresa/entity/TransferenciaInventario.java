package com.serviteca.multiempresa.entity;

import com.serviteca.shared.util.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transferencias_inventario")
public class TransferenciaInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "sede_id")
    private Long sedeId;

    @Column(name = "sede_origen_id", nullable = false)
    private Long sedeOrigenId;

    @Column(name = "sede_destino_id", nullable = false)
    private Long sedeDestinoId;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(length = 50)
    private String usuario;

    @Column(length = 500)
    private String observaciones;

    @OneToMany(mappedBy = "transferencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransferenciaProducto> productos = new ArrayList<>();

    @PrePersist
    protected void onCreate() { com.serviteca.shared.util.TenantUtil.setTenantFields(this); if (fecha == null) fecha = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }
    public Long getSedeOrigenId() { return sedeOrigenId; }
    public void setSedeOrigenId(Long sedeOrigenId) { this.sedeOrigenId = sedeOrigenId; }
    public Long getSedeDestinoId() { return sedeDestinoId; }
    public void setSedeDestinoId(Long sedeDestinoId) { this.sedeDestinoId = sedeDestinoId; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public List<TransferenciaProducto> getProductos() { return productos; }
    public void setProductos(List<TransferenciaProducto> productos) { this.productos = productos; }
}
