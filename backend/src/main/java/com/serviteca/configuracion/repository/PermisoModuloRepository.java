package com.serviteca.configuracion.repository;

import com.serviteca.configuracion.entity.PermisoModulo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PermisoModuloRepository extends JpaRepository<PermisoModulo, Long> {
    List<PermisoModulo> findByRolId(Long rolId);
    Optional<PermisoModulo> findByRolIdAndModuloAndPermiso(Long rolId, String modulo, String permiso);
}
