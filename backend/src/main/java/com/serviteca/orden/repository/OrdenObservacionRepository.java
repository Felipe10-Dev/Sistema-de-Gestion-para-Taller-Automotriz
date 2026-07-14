package com.serviteca.orden.repository;

import com.serviteca.orden.entity.OrdenObservacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenObservacionRepository extends JpaRepository<OrdenObservacion, Long> {
    List<OrdenObservacion> findByOrdenIdOrderByFechaAsc(Long ordenId);
    List<OrdenObservacion> findByOrdenIdIn(List<Long> ordenIds);
}
