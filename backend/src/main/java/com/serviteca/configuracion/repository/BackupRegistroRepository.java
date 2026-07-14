package com.serviteca.configuracion.repository;

import com.serviteca.configuracion.entity.BackupRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BackupRegistroRepository extends JpaRepository<BackupRegistro, Long> {
    List<BackupRegistro> findAllByOrderByFechaDesc();
}
