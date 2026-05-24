package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {

    // ===================================
    // CONSULTAS DERIVADAS
    // ===================================

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByActivoTrue();

    // ===================================
    // JPQL
    // ===================================

    @Query("""
           SELECT u
           FROM Usuario u
           JOIN FETCH u.rol r
           WHERE r.nombre = :rol
           ORDER BY u.email ASC
           """)
    List<Usuario> buscarPorRol(
            @Param("rol") String rol);

    @Query("""
           SELECT u
           FROM Usuario u
           WHERE u.ultimoAcceso IS NOT NULL
           AND u.ultimoAcceso >= :fecha
           ORDER BY u.ultimoAcceso DESC
           """)
    List<Usuario> usuariosConAccesoReciente(
            @Param("fecha")
            LocalDateTime fecha);

    @Query("""
           SELECT r.nombre, COUNT(u)
           FROM Usuario u
           JOIN u.rol r
           GROUP BY r.nombre
           ORDER BY COUNT(u) DESC
           """)
    List<Object[]> contarUsuariosPorRol();
}