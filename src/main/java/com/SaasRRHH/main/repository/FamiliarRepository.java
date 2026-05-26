package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.Familiar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FamiliarRepository
        extends JpaRepository<Familiar, Long> {

    // ===================================
    // CONSULTAS DERIVADAS
    // ===================================

    List<Familiar>
    findByEmpleadoId(Long empleadoId);

    List<Familiar>
    findByActivoTrue();

    // ===================================
    // JPQL
    // ===================================

    @Query("""
           SELECT f
           FROM Familiar f
           JOIN FETCH f.empleado e
           WHERE f.parentesco = :parentesco
           ORDER BY f.nombres ASC
           """)
    List<Familiar>
    buscarPorParentesco(
            @Param("parentesco")
            Familiar.Parentesco parentesco);

    @Query("""
           SELECT f
           FROM Familiar f
           JOIN FETCH f.empleado e
           WHERE f.estudia = true
           ORDER BY f.fechaNacimiento DESC
           """)
    List<Familiar>
    familiaresQueEstudian();

    @Query("""
           SELECT f.parentesco, COUNT(f)
           FROM Familiar f
           GROUP BY f.parentesco
           ORDER BY COUNT(f) DESC
           """)
    List<Object[]>
    contarPorParentesco();
}
