package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

   Optional<Empleado> findByDni(String dni);

   List<Empleado> findByActivoTrue();

   // ✅ TU VERSIÓN (Nancy) - con @Query explícita
   @Query("SELECT e FROM Empleado e WHERE e.usuario.id = :usuarioId")
   Optional<Empleado> findByUsuarioId(@Param("usuarioId") Long usuarioId);

   // ✅ AGREGADO POR MIGUEL - Supervisores
   @Query("""
       SELECT DISTINCT t.supervisor
       FROM TareaAsignada t
       WHERE t.supervisor IS NOT NULL
       ORDER BY t.supervisor.apellidos ASC
   """)
   List<Empleado> findSupervisores();

   // ✅ AGREGADO POR MIGUEL - Trabajadores (no supervisores)
   @Query("""
       SELECT e
       FROM Empleado e
       WHERE e.id NOT IN (
           SELECT DISTINCT t.supervisor.id
           FROM TareaAsignada t
           WHERE t.supervisor IS NOT NULL
       )
       AND e.activo = true
       ORDER BY e.apellidos ASC
   """)
   List<Empleado> findTrabajadores();

   // ✅ AGREGADO POR MIGUEL - Trabajadores por rol
   @Query("""
       SELECT e
       FROM Empleado e
       JOIN e.usuario u
       JOIN u.rol r
       WHERE r.nombreRol IN ('TRABAJADOR', 'EMPLEADO')
       AND e.activo = true
       AND r.nombreRol != 'ADMIN'
       ORDER BY e.apellidos ASC
   """)
   List<Empleado> findTrabajadoresByRol();

   // ✅ AGREGADO POR MIGUEL - Supervisores por rol
   @Query("""
       SELECT e
       FROM Empleado e
       JOIN e.usuario u
       JOIN u.rol r
       WHERE r.nombreRol = 'SUPERVISOR'
       AND e.activo = true
       ORDER BY e.apellidos ASC
   """)
   List<Empleado> findSupervisoresByRol();

   @Query("""
         SELECT e
         FROM Empleado e
         WHERE e.cargo = :cargo
         ORDER BY e.apellidos ASC
         """)
   List<Empleado> buscarPorCargo(@Param("cargo") String cargo);

   @Query("""
         SELECT e
         FROM Empleado e
         WHERE e.cargo = :cargo
         AND e.activo = :activo
         ORDER BY e.fechaRegistro DESC
         """)
   List<Empleado> buscarPorCargoYEstado(
         @Param("cargo") String cargo,
         @Param("activo") Boolean activo);

   @Query("""
         SELECT e
         FROM Empleado e
         JOIN FETCH e.usuario u
         WHERE e.activo = true
         ORDER BY e.apellidos ASC
         """)
   List<Empleado> listarActivosConUsuario();

   @Query("""
         SELECT e
         FROM Empleado e
         WHERE e.fechaFinContrato IS NOT NULL
         AND e.fechaFinContrato < CURRENT_DATE
         ORDER BY e.fechaFinContrato ASC
         """)
   List<Empleado> contratosVencidos();

   @Query("""
         SELECT e
         FROM Empleado e
         WHERE e.fechaFinContrato
         BETWEEN CURRENT_DATE AND :fechaLimite
         ORDER BY e.fechaFinContrato ASC
         """)
   List<Empleado> contratosPorVencer(@Param("fechaLimite") LocalDate fechaLimite);

   @Query("""
         SELECT e.cargo, COUNT(e)
         FROM Empleado e
         GROUP BY e.cargo
         ORDER BY COUNT(e) DESC
         """)
   List<Object[]> contarEmpleadosPorCargo();
}