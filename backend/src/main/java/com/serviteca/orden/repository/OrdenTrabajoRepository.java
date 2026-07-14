package com.serviteca.orden.repository;

import com.serviteca.orden.entity.OrdenTrabajo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Long> {
    Optional<OrdenTrabajo> findByNumeroOrden(String numeroOrden);
    boolean existsByNumeroOrden(String numeroOrden);

    @EntityGraph(attributePaths = {"cliente", "vehiculo", "tecnico"})
    @Query("SELECT o FROM OrdenTrabajo o WHERE " +
           "(LOWER(o.numeroOrden) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(o.cliente.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(o.cliente.apellido) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(o.vehiculo.placa) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<OrdenTrabajo> search(String search, Pageable pageable);

    @EntityGraph(attributePaths = {"cliente", "vehiculo", "tecnico"})
    Page<OrdenTrabajo> findByEstado(String estado, Pageable pageable);

    @EntityGraph(attributePaths = {"cliente", "vehiculo", "tecnico"})
    @Query("SELECT o FROM OrdenTrabajo o WHERE o.estado NOT IN ('ENTREGADO', 'CANCELADO')")
    Page<OrdenTrabajo> findActivas(Pageable pageable);

    @EntityGraph(attributePaths = {"cliente", "vehiculo", "tecnico"})
    @Query("SELECT o FROM OrdenTrabajo o WHERE " +
           "(COALESCE(:estado, o.estado) = o.estado) AND " +
           "(COALESCE(:estadoFinanciero, o.estadoFinanciero) = o.estadoFinanciero) AND " +
           "(COALESCE(:clienteId, o.cliente.id) = o.cliente.id) AND " +
           "(COALESCE(:vehiculoId, o.vehiculo.id) = o.vehiculo.id) AND " +
           "(COALESCE(:fechaInicio, o.fechaIngreso) <= o.fechaIngreso) AND " +
           "(COALESCE(:fechaFin, o.fechaIngreso) >= o.fechaIngreso)")
    Page<OrdenTrabajo> findByFilters(@Param("estado") String estado,
                                      @Param("estadoFinanciero") String estadoFinanciero,
                                      @Param("clienteId") Long clienteId,
                                      @Param("vehiculoId") Long vehiculoId,
                                      @Param("fechaInicio") LocalDateTime fechaInicio,
                                      @Param("fechaFin") LocalDateTime fechaFin,
                                      Pageable pageable);

    @EntityGraph(attributePaths = {"cliente", "vehiculo", "tecnico"})
    @Override
    Optional<OrdenTrabajo> findById(Long id);

    long countByEstado(String estado);

    long countByEstadoAndFechaSalidaBetween(String estado, LocalDateTime inicio, LocalDateTime fin);

    long countByEstadoFinanciero(String estadoFinanciero);

    @Query("SELECT COALESCE(SUM(o.totalGeneral), 0) FROM OrdenTrabajo o " +
           "WHERE o.estado = :estado AND o.fechaSalida BETWEEN :inicio AND :fin")
    double sumTotalGeneralByEstadoAndFechaSalidaBetween(@Param("estado") String estado,
                                                         @Param("inicio") LocalDateTime inicio,
                                                         @Param("fin") LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(o.totalGeneral - COALESCE(" +
           "(SELECT SUM(p.valor) FROM com.serviteca.caja.entity.Pago p WHERE p.orden = o AND p.pagoAnulado = false), 0)), 0) " +
           "FROM OrdenTrabajo o WHERE o.estadoFinanciero IN ('SIN_PAGAR', 'PARCIAL')")
    double sumSaldoPendienteTotal();

    @EntityGraph(attributePaths = {"cliente", "vehiculo", "tecnico"})
    List<OrdenTrabajo> findByVehiculoIdOrderByFechaIngresoDesc(Long vehiculoId);

    long countByVehiculoId(Long vehiculoId);

    @Query("SELECT COALESCE(SUM(o.totalGeneral), 0) FROM OrdenTrabajo o WHERE o.vehiculo.id = :vehiculoId")
    BigDecimal sumTotalGeneralByVehiculoId(@Param("vehiculoId") Long vehiculoId);

    @Query("SELECT MAX(o.fechaIngreso) FROM OrdenTrabajo o WHERE o.vehiculo.id = :vehiculoId")
    LocalDateTime findMaxFechaIngresoByVehiculoId(@Param("vehiculoId") Long vehiculoId);

    @Query("SELECT o.vehiculo.id FROM OrdenTrabajo o WHERE o.vehiculo.activo = true " +
           "GROUP BY o.vehiculo.id HAVING MAX(o.fechaIngreso) < :fechaLimite")
    List<Long> findVehiculosSinVisitasDesde(@Param("fechaLimite") LocalDateTime fechaLimite);

    @Query("SELECT o.cliente.id, COUNT(o) as cnt FROM OrdenTrabajo o WHERE o.cliente.activo = true " +
           "GROUP BY o.cliente.id ORDER BY cnt DESC")
    List<Object[]> findClientesFrecuentes(Pageable pageable);

    @Query("SELECT o.vehiculo.id, SUM(o.totalGeneral) as total FROM OrdenTrabajo o WHERE o.vehiculo.activo = true " +
           "GROUP BY o.vehiculo.id ORDER BY total DESC")
    List<Object[]> findVehiculosMayorInversion(Pageable pageable);

    @EntityGraph(attributePaths = {"cliente", "vehiculo", "tecnico"})
    List<OrdenTrabajo> findByClienteIdOrderByFechaIngresoDesc(Long clienteId);

    long countByClienteId(Long clienteId);

    @Query("SELECT COALESCE(SUM(o.totalGeneral), 0) FROM OrdenTrabajo o WHERE o.cliente.id = :clienteId")
    BigDecimal sumTotalGeneralByClienteId(@Param("clienteId") Long clienteId);

    @Query("SELECT MAX(o.fechaIngreso) FROM OrdenTrabajo o WHERE o.cliente.id = :clienteId")
    Optional<LocalDateTime> findMaxFechaIngresoByClienteId(@Param("clienteId") Long clienteId);

    @Query("SELECT MIN(o.fechaIngreso) FROM OrdenTrabajo o WHERE o.cliente.id = :clienteId")
    Optional<LocalDateTime> findMinFechaIngresoByClienteId(@Param("clienteId") Long clienteId);

    @Query("SELECT MAX(o.totalGeneral) FROM OrdenTrabajo o WHERE o.cliente.id = :clienteId")
    Optional<BigDecimal> findMaxTotalGeneralByClienteId(@Param("clienteId") Long clienteId);

    @Query("SELECT COALESCE(SUM(o.totalGeneral), 0) FROM OrdenTrabajo o WHERE o.cliente.id = :clienteId " +
           "AND o.estadoFinanciero != 'PAGADA'")
    BigDecimal sumTotalPendienteByClienteId(@Param("clienteId") Long clienteId);

    @Query("SELECT o.cliente.id, SUM(o.totalGeneral) as total FROM OrdenTrabajo o WHERE o.cliente.activo = true " +
           "GROUP BY o.cliente.id ORDER BY total DESC")
    List<Object[]> findClientesByTotalGastado(Pageable pageable);

    @Query("SELECT COUNT(DISTINCT o.cliente.id) FROM OrdenTrabajo o WHERE o.fechaIngreso >= :desde")
    long countClientesActivosDesde(@Param("desde") LocalDateTime desde);

    long countByEmpresaIdAndEstado(Long empresaId, String estado);
    long countByEmpresaIdAndEstadoFinanciero(Long empresaId, String estadoFinanciero);

    @Query("SELECT COALESCE(SUM(o.totalGeneral), 0) FROM OrdenTrabajo o WHERE o.empresaId = :empresaId")
    BigDecimal sumTotalGeneralByEmpresaId(@Param("empresaId") Long empresaId);
}
