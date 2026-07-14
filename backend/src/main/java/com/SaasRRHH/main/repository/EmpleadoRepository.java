package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    List<Empleado> findByActivoTrue();
    
    Optional<Empleado> findByUsuarioId(Long usuarioId);
    
    Optional<Empleado> findByDni(String dni);
    
    boolean existsByDni(String dni);
    
    List<Empleado> findByCargoContainingIgnoreCase(String cargo);
    
    List<Empleado> findByFechaInicioContratoBetween(LocalDate inicio, LocalDate fin);
    
    @Query("SELECT e FROM Empleado e WHERE e.fechaFinContrato IS NOT NULL AND e.fechaFinContrato < :fecha")
    List<Empleado> findContratosVencidos(@Param("fecha") LocalDate fecha);

    // ============================================
    // ✅ NUEVAS QUERIES PARA HORARIOS Y FILTROS
    // ============================================

    /**
     * Busca empleados activos con su horario configurado
     */
    @Query("""
       SELECT e
       FROM Empleado e
       LEFT JOIN FETCH e.usuario u
       WHERE e.activo = true
       ORDER BY e.apellidos, e.nombres
       """)
    List<Empleado> findActivosConHorario();

    /**
     * Busca empleados por área de trabajo (si existe la relación)
     */
    @Query("""
       SELECT e
       FROM Empleado e
       WHERE e.activo = true
       AND e.cargo LIKE %:area%
       ORDER BY e.apellidos, e.nombres
       """)
    List<Empleado> findByAreaTrabajo(@Param("area") String area);

    /**
     * Busca empleados que trabajan en un día específico de la semana
     */
    @Query("""
       SELECT e
       FROM Empleado e
       WHERE e.activo = true
       AND e.diasLaborables LIKE %:dia%
       ORDER BY e.apellidos, e.nombres
       """)
    List<Empleado> findEmpleadosPorDiaLaborable(@Param("dia") String dia);

    /**
     * Busca empleados con un tipo de pago específico
     */
    @Query("""
       SELECT e
       FROM Empleado e
       WHERE e.activo = true
       AND e.tipoPago = :tipoPago
       ORDER BY e.apellidos, e.nombres
       """)
    List<Empleado> findByTipoPago(@Param("tipoPago") String tipoPago);

    /**
     * Empleados que deberían estar trabajando ahora (según su horario)
     */
    @Query("""
       SELECT e
       FROM Empleado e
       WHERE e.activo = true
       AND e.horaEntrada <= :horaActual
       AND e.horaSalida >= :horaActual
       ORDER BY e.apellidos, e.nombres
       """)
    List<Empleado> findEmpleadosEnHorarioLaboral(@Param("horaActual") LocalTime horaActual);

    /**
     * Busca empleados con tolerancia de tardanza específica
     */
    @Query("""
       SELECT e
       FROM Empleado e
       WHERE e.activo = true
       AND e.toleranciaMinutos >= :minutos
       ORDER BY e.toleranciaMinutos DESC
       """)
    List<Empleado> findByToleranciaMinima(@Param("minutos") Integer minutos);

    /**
     * Obtiene estadísticas de tipos de pago
     */
    @Query("""
       SELECT e.tipoPago, COUNT(e)
       FROM Empleado e
       WHERE e.activo = true
       GROUP BY e.tipoPago
       """)
    List<Object[]> contarPorTipoPago();

    /**
     * Busca empleados sin horario configurado
     */
    @Query("""
       SELECT e
       FROM Empleado e
       WHERE e.activo = true
       AND (e.horaEntrada IS NULL OR e.horaSalida IS NULL OR e.diasLaborables IS NULL)
       ORDER BY e.apellidos, e.nombres
       """)
    List<Empleado> findEmpleadosSinHorario();
}