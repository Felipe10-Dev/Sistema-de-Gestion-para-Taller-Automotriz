package com.serviteca.multiempresa.repository;

import com.serviteca.multiempresa.entity.TransferenciaInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransferenciaInventarioRepository extends JpaRepository<TransferenciaInventario, Long> {
    List<TransferenciaInventario> findByEmpresaIdOrderByFechaDesc(Long empresaId);
    List<TransferenciaInventario> findBySedeOrigenIdOrSedeDestinoIdOrderByFechaDesc(Long sedeOrigenId, Long sedeDestinoId);
}
