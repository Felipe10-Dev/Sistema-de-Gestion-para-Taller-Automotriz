package com.serviteca.orden.repository;

import com.serviteca.orden.entity.HistorialEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Long> {
    List<HistorialEstado> findByOrdenIdOrderByFechaAsc(Long ordenId);
    List<HistorialEstado> findByOrdenIdIn(List<Long> ordenIds);
}
