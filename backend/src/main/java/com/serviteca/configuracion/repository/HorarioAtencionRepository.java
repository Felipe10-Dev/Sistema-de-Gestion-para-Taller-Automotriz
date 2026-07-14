package com.serviteca.configuracion.repository;

import com.serviteca.configuracion.entity.HorarioAtencion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface HorarioAtencionRepository extends JpaRepository<HorarioAtencion, Long> {
    Optional<HorarioAtencion> findByDiaSemanaIgnoreCase(String diaSemana);
}
