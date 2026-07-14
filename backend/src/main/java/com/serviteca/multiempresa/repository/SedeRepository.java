package com.serviteca.multiempresa.repository;

import com.serviteca.multiempresa.entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SedeRepository extends JpaRepository<Sede, Long> {
    List<Sede> findByEmpresaId(Long empresaId);
    List<Sede> findByEmpresaIdAndActivoTrue(Long empresaId);
}
