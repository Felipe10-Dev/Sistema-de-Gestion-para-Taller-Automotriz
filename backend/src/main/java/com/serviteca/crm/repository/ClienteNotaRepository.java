package com.serviteca.crm.repository;

import com.serviteca.crm.entity.ClienteNota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteNotaRepository extends JpaRepository<ClienteNota, Long> {
    List<ClienteNota> findByClienteIdOrderByFechaDesc(Long clienteId);
}
