package com.serviteca.producto.repository;

import com.serviteca.producto.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);

    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.categoria LEFT JOIN FETCH p.proveedor WHERE p.activo = true AND " +
           "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "p.codigo LIKE CONCAT('%', :search, '%'))")
    Page<Producto> search(@Param("search") String search, Pageable pageable);

    @EntityGraph(attributePaths = {"categoria", "proveedor"})
    List<Producto> findByCategoriaId(Long categoriaId);

    @EntityGraph(attributePaths = {"categoria", "proveedor"})
    List<Producto> findByProveedorId(Long proveedorId);

    long countByEmpresaId(Long empresaId);
    long countByEmpresaIdAndActivoTrue(Long empresaId);

    @EntityGraph(attributePaths = {"categoria", "proveedor"})
    Page<Producto> findByActivoTrue(Pageable pageable);

    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.categoria LEFT JOIN FETCH p.proveedor WHERE p.activo = true AND " +
           "(:categoriaId IS NULL OR p.categoria.id = :categoriaId) AND " +
           "(:proveedorId IS NULL OR p.proveedor.id = :proveedorId)")
    Page<Producto> findByFilters(@Param("categoriaId") Long categoriaId,
                                  @Param("proveedorId") Long proveedorId,
                                  Pageable pageable);

    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.categoria LEFT JOIN FETCH p.proveedor WHERE p.activo = true AND " +
           "EXISTS (SELECT i FROM Inventario i WHERE i.producto = p AND i.cantidadActual <= i.cantidadMinima)")
    Page<Producto> findBajoStock(Pageable pageable);

    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.categoria LEFT JOIN FETCH p.proveedor WHERE p.activo = true AND " +
           "NOT EXISTS (SELECT i FROM Inventario i WHERE i.producto = p AND i.cantidadActual > 0)")
    Page<Producto> findSinStock(Pageable pageable);
}
