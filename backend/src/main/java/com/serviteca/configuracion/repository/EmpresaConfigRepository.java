package com.serviteca.configuracion.repository;

import com.serviteca.configuracion.entity.EmpresaConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmpresaConfigRepository extends JpaRepository<EmpresaConfig, Long> {
    Optional<EmpresaConfig> findByActivoTrue();
}
