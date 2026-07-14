package com.serviteca.configuracion.repository;

import com.serviteca.configuracion.entity.ParametroSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ParametroSistemaRepository extends JpaRepository<ParametroSistema, Long> {
    Optional<ParametroSistema> findByCodigo(String codigo);
}
