package com.serviteca.orden.repository;

import com.serviteca.orden.entity.OrdenProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenProductoRepository extends JpaRepository<OrdenProducto, Long> {
    List<OrdenProducto> findByOrdenId(Long ordenId);
    List<OrdenProducto> findByOrdenIdIn(List<Long> ordenIds);
    void deleteByOrdenId(Long ordenId);
}
