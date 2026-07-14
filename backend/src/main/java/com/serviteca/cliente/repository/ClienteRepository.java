package com.serviteca.cliente.repository;

import com.serviteca.cliente.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByNumeroDocumento(String numeroDocumento);
    boolean existsByNumeroDocumento(String numeroDocumento);

    @Query("SELECT c FROM Cliente c WHERE c.activo = true AND " +
           "(LOWER(c.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.apellido) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "c.numeroDocumento LIKE CONCAT('%', :search, '%') OR " +
           "c.telefono LIKE CONCAT('%', :search, '%'))")
    Page<Cliente> search(String search, Pageable pageable);

    Page<Cliente> findByActivoTrue(Pageable pageable);

    @Query("SELECT c FROM Cliente c WHERE c.activo = true AND c.id NOT IN " +
           "(SELECT DISTINCT o.cliente.id FROM OrdenTrabajo o WHERE o.fechaIngreso >= :fechaLimite)")
    List<Cliente> findClientesSinVisitasDesde(@Param("fechaLimite") LocalDateTime fechaLimite);

    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.fechaCreacion >= :inicioMes")
    long countNuevosEsteMes(@Param("inicioMes") LocalDateTime inicioMes);

    Page<Cliente> findByActivo(boolean activo, Pageable pageable);

    @Query("SELECT c FROM Cliente c WHERE c.clasificacion = 'VIP'")
    Page<Cliente> findVip(Pageable pageable);

    @Query("SELECT c FROM Cliente c WHERE c.activo = true AND " +
           "c.id IN (SELECT o.cliente.id FROM OrdenTrabajo o GROUP BY o.cliente.id " +
           "HAVING COUNT(o) >= :minOrdenes)")
    Page<Cliente> findFrecuentes(@Param("minOrdenes") long minOrdenes, Pageable pageable);

    @Query("SELECT c FROM Cliente c WHERE c.activo = true AND " +
           "c.fechaCreacion >= :desde")
    Page<Cliente> findNuevos(@Param("desde") LocalDateTime desde, Pageable pageable);

    long countByEmpresaId(Long empresaId);
    long countByEmpresaIdAndActivoTrue(Long empresaId);
}
