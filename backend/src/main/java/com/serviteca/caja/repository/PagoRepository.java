package com.serviteca.caja.repository;

import com.serviteca.caja.entity.Pago;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    @EntityGraph(attributePaths = {"orden", "metodoPago"})
    List<Pago> findByOrdenIdOrderByFechaAsc(Long ordenId);

    @Query("SELECT COALESCE(SUM(p.valor), 0) FROM Pago p WHERE p.orden.id = :ordenId AND p.pagoAnulado = false")
    BigDecimal sumByOrdenIdAndAnuladoFalse(@Param("ordenId") Long ordenId);

    @Query("SELECT COALESCE(SUM(p.valor), 0) FROM Pago p WHERE p.fecha BETWEEN :inicio AND :fin AND p.pagoAnulado = false")
    BigDecimal sumByFechaBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(p.valor), 0) FROM Pago p WHERE p.fecha >= :inicio AND p.pagoAnulado = false")
    BigDecimal sumDesde(@Param("inicio") LocalDateTime inicio);

    @EntityGraph(attributePaths = {"orden", "metodoPago"})
    List<Pago> findAllByOrderByFechaDesc();

    @Query("SELECT COALESCE(SUM(p.valor), 0) FROM Pago p WHERE p.orden.cliente.id = :clienteId AND p.pagoAnulado = false")
    BigDecimal sumByClienteIdAndAnuladoFalse(@Param("clienteId") Long clienteId);
}
