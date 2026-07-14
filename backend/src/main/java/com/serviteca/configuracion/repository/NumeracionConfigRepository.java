package com.serviteca.configuracion.repository;

import com.serviteca.configuracion.entity.NumeracionConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface NumeracionConfigRepository extends JpaRepository<NumeracionConfig, Long> {
    Optional<NumeracionConfig> findByModulo(String modulo);

    @Query("UPDATE NumeracionConfig n SET n.numeroActual = n.numeroActual + 1 WHERE n.modulo = :modulo")
    int incrementarNumero(@Param("modulo") String modulo);
}
