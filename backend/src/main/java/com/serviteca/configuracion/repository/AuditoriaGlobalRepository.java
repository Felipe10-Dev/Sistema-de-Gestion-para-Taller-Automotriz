package com.serviteca.configuracion.repository;

import com.serviteca.configuracion.entity.AuditoriaGlobal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditoriaGlobalRepository extends JpaRepository<AuditoriaGlobal, Long> {
    List<AuditoriaGlobal> findAllByOrderByFechaDesc();
    List<AuditoriaGlobal> findByModuloOrderByFechaDesc(String modulo);
    List<AuditoriaGlobal> findByUsuarioOrderByFechaDesc(String usuario);
}
