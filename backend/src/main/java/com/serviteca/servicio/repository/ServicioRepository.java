package com.serviteca.servicio.repository;

import com.serviteca.servicio.entity.Servicio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    Page<Servicio> findByActivoTrue(Pageable pageable);

    @Query("SELECT s FROM Servicio s WHERE s.activo = true AND " +
           "(LOWER(s.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.descripcion) LIKE LOWER(CONCAT('%', :search, '%')))")
    org.springframework.data.domain.Page<Servicio> search(String search, org.springframework.data.domain.Pageable pageable);

    List<Servicio> findByCategoriaId(Long categoriaId);
}
