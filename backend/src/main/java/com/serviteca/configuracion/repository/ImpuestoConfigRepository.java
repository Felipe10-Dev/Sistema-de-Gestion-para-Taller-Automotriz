package com.serviteca.configuracion.repository;

import com.serviteca.configuracion.entity.ImpuestoConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ImpuestoConfigRepository extends JpaRepository<ImpuestoConfig, Long> {
    Optional<ImpuestoConfig> findByAplicacionPorDefectoTrue();
}
