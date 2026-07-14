package com.serviteca.vehiculo.repository;

import com.serviteca.vehiculo.entity.Vehiculo;
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
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    Optional<Vehiculo> findByPlaca(String placa);
    boolean existsByPlaca(String placa);
    List<Vehiculo> findByClienteId(Long clienteId);

    @Query("SELECT v FROM Vehiculo v WHERE v.activo = true AND " +
           "(LOWER(v.placa) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(v.marca) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(v.linea) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(v.vin) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Vehiculo> search(String search, Pageable pageable);

    long countByEmpresaId(Long empresaId);
    long countByEmpresaIdAndActivoTrue(Long empresaId);

    @Query("SELECT v.cliente.id, COUNT(v) FROM Vehiculo v WHERE v.cliente.activo = true GROUP BY v.cliente.id ORDER BY COUNT(v) DESC")
    List<Object[]> countVehiculosPorCliente();

    @Query(value = "SELECT * FROM vehiculos v WHERE v.activo = true AND " +
           "(:marca IS NULL OR v.marca ILIKE CONCAT('%', CAST(:marca AS TEXT), '%')) AND " +
           "(:modelo IS NULL OR v.modelo ILIKE CONCAT('%', CAST(:modelo AS TEXT), '%')) AND " +
           "(:anio IS NULL OR v.anio = CAST(:anio AS INTEGER))",
           countQuery = "SELECT COUNT(*) FROM vehiculos v WHERE v.activo = true AND " +
           "(:marca IS NULL OR v.marca ILIKE CONCAT('%', CAST(:marca AS TEXT), '%')) AND " +
           "(:modelo IS NULL OR v.modelo ILIKE CONCAT('%', CAST(:modelo AS TEXT), '%')) AND " +
           "(:anio IS NULL OR v.anio = CAST(:anio AS INTEGER))",
           nativeQuery = true)
    Page<Vehiculo> findByFilters(@Param("marca") String marca,
                                  @Param("modelo") String modelo,
                                  @Param("anio") Integer anio,
                                  Pageable pageable);
}
