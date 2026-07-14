package com.serviteca.proveedor.repository;

import com.serviteca.proveedor.entity.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    Optional<Proveedor> findByNit(String nit);
    boolean existsByNit(String nit);

    @Query("SELECT p FROM Proveedor p WHERE p.activo = true AND " +
           "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "p.nit LIKE CONCAT('%', :search, '%') OR " +
           "LOWER(p.contacto) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Proveedor> search(String search, Pageable pageable);

    Page<Proveedor> findByActivoTrue(Pageable pageable);

    long countByEmpresaId(Long empresaId);
    long countByEmpresaIdAndActivoTrue(Long empresaId);
}
