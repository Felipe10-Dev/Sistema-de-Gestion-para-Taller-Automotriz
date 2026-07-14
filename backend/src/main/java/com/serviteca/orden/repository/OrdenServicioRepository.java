package com.serviteca.orden.repository;

import com.serviteca.orden.entity.OrdenServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenServicioRepository extends JpaRepository<OrdenServicio, Long> {
    List<OrdenServicio> findByOrdenId(Long ordenId);
    List<OrdenServicio> findByOrdenIdIn(List<Long> ordenIds);
    void deleteByOrdenId(Long ordenId);

    @Query("SELECT os.orden.cliente.id, COUNT(os) as cnt FROM OrdenServicio os WHERE os.orden.cliente.activo = true " +
           "GROUP BY os.orden.cliente.id ORDER BY cnt DESC")
    List<Object[]> countByClienteGrouped();
}
