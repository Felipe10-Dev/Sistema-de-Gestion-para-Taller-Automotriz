package com.serviteca.configuracion.repository;

import com.serviteca.configuracion.entity.DiaFestivo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface DiaFestivoRepository extends JpaRepository<DiaFestivo, Long> {
    Optional<DiaFestivo> findByFecha(LocalDate fecha);
}
