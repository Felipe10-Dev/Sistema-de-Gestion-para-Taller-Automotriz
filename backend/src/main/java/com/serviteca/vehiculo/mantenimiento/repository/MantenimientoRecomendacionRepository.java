package com.serviteca.vehiculo.mantenimiento.repository;

import com.serviteca.vehiculo.mantenimiento.entity.MantenimientoRecomendacion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MantenimientoRecomendacionRepository extends JpaRepository<MantenimientoRecomendacion, Long> {
    @EntityGraph(attributePaths = {"vehiculo"})
    List<MantenimientoRecomendacion> findByVehiculoIdOrderByTipoAsc(Long vehiculoId);

    @EntityGraph(attributePaths = {"vehiculo"})
    List<MantenimientoRecomendacion> findByVehiculoIdAndActivoTrue(Long vehiculoId);

    @EntityGraph(attributePaths = {"vehiculo", "vehiculo.cliente"})
    @Query("SELECT m FROM MantenimientoRecomendacion m WHERE m.activo = true")
    List<MantenimientoRecomendacion> findAllActivos();
}
