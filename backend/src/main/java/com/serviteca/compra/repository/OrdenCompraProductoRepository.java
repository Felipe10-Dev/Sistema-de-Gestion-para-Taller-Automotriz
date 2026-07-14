package com.serviteca.compra.repository;

import com.serviteca.compra.entity.OrdenCompraProducto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenCompraProductoRepository extends JpaRepository<OrdenCompraProducto, Long> {
    @EntityGraph(attributePaths = {"producto", "ordenCompra", "ordenCompra.proveedor"})
    List<OrdenCompraProducto> findByOrdenCompraId(Long ordenCompraId);

    @Query("SELECT DISTINCT op.producto.id FROM OrdenCompraProducto op WHERE op.ordenCompra.proveedor.id = :proveedorId")
    List<Long> findProductoIdsByProveedorId(@Param("proveedorId") Long proveedorId);

    @EntityGraph(attributePaths = {"ordenCompra", "ordenCompra.proveedor"})
    List<OrdenCompraProducto> findByProductoIdOrderByOrdenCompraFechaDesc(Long productoId);
}
