package com.serviteca.inventario.repository;

import com.serviteca.inventario.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    @Query("SELECT i FROM Inventario i JOIN FETCH i.producto WHERE i.producto.id = :productoId")
    Optional<Inventario> findByProductoId(@Param("productoId") Long productoId);

    @Query("SELECT i FROM Inventario i JOIN FETCH i.producto WHERE i.producto.id IN :productoIds")
    List<Inventario> findByProductoIdIn(@Param("productoIds") List<Long> productoIds);

    @Query("SELECT i FROM Inventario i JOIN FETCH i.producto WHERE i.cantidadActual <= i.cantidadMinima")
    List<Inventario> findProductosBajoStock();

    @Query("SELECT i FROM Inventario i JOIN FETCH i.producto WHERE i.cantidadActual > 0")
    List<Inventario> findProductosConStock();

    long countByCantidadActual(int cantidadActual);

    @Query("SELECT COALESCE(SUM(i.cantidadActual * p.precioVenta), 0) FROM Inventario i JOIN i.producto p WHERE i.cantidadActual > 0")
    double sumValorTotalInventario();
}
