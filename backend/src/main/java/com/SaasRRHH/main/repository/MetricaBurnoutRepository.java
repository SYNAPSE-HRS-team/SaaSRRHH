package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.MetricaBurnout;
import com.SaasRRHH.main.model.TareaAsignada;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MetricaBurnoutRepository
        extends JpaRepository<MetricaBurnout, Long> {

    @Query("SELECT t FROM TareaAsignada t WHERE t.empleado.id = :empleadoId AND t.fecha BETWEEN :inicio AND :fin")
    List<TareaAsignada> findByEmpleadoIdAndFechaBetween(
            @Param("empleadoId") Long empleadoId,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin
    );

    @Query("SELECT m FROM MetricaBurnout m JOIN FETCH m.empleado e JOIN FETCH e.usuario")
    List<MetricaBurnout> findAllWithRelaciones();

    @Query("SELECT m FROM MetricaBurnout m JOIN FETCH m.empleado e JOIN FETCH e.usuario WHERE m.id = :id")
    Optional<MetricaBurnout> findByIdWithRelaciones(@Param("id") Long id);

    @Query("SELECT m FROM MetricaBurnout m JOIN FETCH m.empleado e JOIN FETCH e.usuario WHERE e.id = :empleadoId")
    List<MetricaBurnout> findByEmpleadoId(@Param("empleadoId") Long empleadoId);

    // ============================================
    // ✅ NUEVAS QUERIES PARA MÉTRICAS AVANZADAS
    // ============================================

    /**
     * Obtiene la última métrica de burnout de un empleado
     */
    @Query(value = """
       SELECT m.*
       FROM metricas_burnout m
       WHERE m.empleado_id = :empleadoId
       ORDER BY m.fecha_evaluacion DESC
       LIMIT 1
       """, nativeQuery = true)
    Optional<MetricaBurnout> findUltimaMetricaByEmpleado(@Param("empleadoId") Long empleadoId);

    /**
     * Encuentra todos los empleados que actualmente tienen riesgo ALTO
     */
    @Query("""
       SELECT m
       FROM MetricaBurnout m
       JOIN FETCH m.empleado e
       JOIN FETCH e.usuario
       WHERE m.nivelRiesgo = 'ALTO'
       AND m.fechaEvaluacion = (
           SELECT MAX(m2.fechaEvaluacion)
           FROM MetricaBurnout m2
           WHERE m2.empleado.id = m.empleado.id
       )
       ORDER BY m.indicePuntualidad ASC
       """)
    List<MetricaBurnout> findEmpleadosConRiesgoAlto();

    /**
     * Encuentra empleados con riesgo MEDIO o ALTO
     */
    @Query("""
       SELECT m
       FROM MetricaBurnout m
       JOIN FETCH m.empleado e
       WHERE m.nivelRiesgo IN ('MEDIO', 'ALTO')
       AND m.fechaEvaluacion = (
           SELECT MAX(m2.fechaEvaluacion)
           FROM MetricaBurnout m2
           WHERE m2.empleado.id = m.empleado.id
       )
       ORDER BY m.nivelRiesgo DESC, m.indicePuntualidad ASC
       """)
    List<MetricaBurnout> findEmpleadosConRiesgo();

    /**
     * Obtiene métricas en un rango de fechas específico
     */
    @Query("""
       SELECT m
       FROM MetricaBurnout m
       JOIN FETCH m.empleado e
       WHERE m.fechaEvaluacion BETWEEN :inicio AND :fin
       ORDER BY m.fechaEvaluacion DESC
       """)
    List<MetricaBurnout> findMetricasEnPeriodo(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Cuenta empleados por nivel de riesgo (última evaluación)
     */
    @Query(value = """
       SELECT m.nivel_riesgo, COUNT(DISTINCT m.empleado_id)
       FROM metricas_burnout m
       WHERE m.fecha_evaluacion = (
           SELECT MAX(m2.fecha_evaluacion)
           FROM metricas_burnout m2
           WHERE m2.empleado_id = m.empleado_id
       )
       GROUP BY m.nivel_riesgo
       """, nativeQuery = true)
    List<Object[]> contarEmpleadosPorNivelRiesgo();

    /**
     * Obtiene el promedio de índice de puntualidad de todos los empleados
     */
    @Query(value = """
       SELECT AVG(m.indice_puntualidad)
       FROM metricas_burnout m
       WHERE m.fecha_evaluacion = (
           SELECT MAX(m2.fecha_evaluacion)
           FROM metricas_burnout m2
           WHERE m2.empleado_id = m.empleado_id
       )
       """, nativeQuery = true)
    Double obtenerPromedioPuntualidadGeneral();

    /**
     * Encuentra empleados con patrón de tardanza detectado
     */
    @Query("""
       SELECT m
       FROM MetricaBurnout m
       JOIN FETCH m.empleado e
       WHERE m.patronDetectado IS NOT NULL
       AND m.patronDetectado != ''
       AND m.fechaEvaluacion = (
           SELECT MAX(m2.fechaEvaluacion)
           FROM MetricaBurnout m2
           WHERE m2.empleado.id = m.empleado.id
       )
       ORDER BY m.fechaEvaluacion DESC
       """)
    List<MetricaBurnout> findEmpleadosConPatronDetectado();
}