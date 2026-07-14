package com.serviteca.caja.repository;

import com.serviteca.caja.entity.Caja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CajaRepository extends JpaRepository<Caja, Long> {
    Optional<Caja> findByUsuarioAndEstado(String usuario, String estado);
    List<Caja> findByUsuarioOrderByFechaAperturaDesc(String usuario);
    List<Caja> findAllByOrderByFechaAperturaDesc();
}
