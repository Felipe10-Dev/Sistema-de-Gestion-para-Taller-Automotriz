package com.serviteca.multiempresa.entity;

import com.serviteca.shared.util.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "transferencia_productos")
public class TransferenciaProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transferencia_id", nullable = false)
    private TransferenciaInventario transferencia;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(nullable = false)
    private int cantidad;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TransferenciaInventario getTransferencia() { return transferencia; }
    public void setTransferencia(TransferenciaInventario transferencia) { this.transferencia = transferencia; }
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
