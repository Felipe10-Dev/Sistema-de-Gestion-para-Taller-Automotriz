package com.serviteca.usuario.repository;

import com.serviteca.usuario.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @EntityGraph(attributePaths = {"rol"})
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);
    Page<Usuario> findByActivoTrue(Pageable pageable);
}
