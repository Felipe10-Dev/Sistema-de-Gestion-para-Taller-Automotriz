package com.serviteca.compra.repository;

import com.serviteca.compra.entity.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {
    Optional<OrdenCompra> findByNumeroOrden(String numeroOrden);
    boolean existsByNumeroOrden(String numeroOrden);

    long countByProveedorId(Long proveedorId);

    long countByEstadoAndFechaBetween(String estado, LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM OrdenCompra o WHERE o.proveedor.id = :proveedorId AND o.estado = 'RECIBIDA'")
    BigDecimal sumTotalByProveedorIdAndEstadoRecibida(@Param("proveedorId") Long proveedorId);

    @Query("SELECT MAX(o.fecha) FROM OrdenCompra o WHERE o.proveedor.id = :proveedorId AND o.estado = 'RECIBIDA'")
    LocalDateTime findMaxFechaByProveedorIdAndEstadoRecibida(@Param("proveedorId") Long proveedorId);

    @Query("SELECT o.proveedor.id, COUNT(o) as cnt FROM OrdenCompra o WHERE o.proveedor.id IS NOT NULL " +
           "GROUP BY o.proveedor.id ORDER BY cnt DESC")
    List<Object[]> findProveedoresMasUtilizados();

    @Query("SELECT o.proveedor.id, COALESCE(AVG(TIMESTAMPDIFF(DAY, o.fecha, COALESCE(o.fechaModificacion, o.fecha))), 0) " +
           "FROM OrdenCompra o WHERE o.estado = 'RECIBIDA' AND o.proveedor.id = :proveedorId GROUP BY o.proveedor.id")
    Optional<Object[]> findPromedioDiasEntregaByProveedorId(@Param("proveedorId") Long proveedorId);

    long countByProveedorIdAndEstado(Long proveedorId, String estado);
}
