package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.RegistroAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroAsistenciaRepository extends JpaRepository<RegistroAsistencia, Long> {
    
    // Buscar por empleado
    List<RegistroAsistencia> findByEmpleadoId(Long empleadoId);
    
    // Buscar por empleado y fecha
    List<RegistroAsistencia> findByEmpleadoIdAndFechaHoraBetween(Long empleadoId, LocalDateTime inicio, LocalDateTime fin);
    
    // Buscar registros de un día específico
    @Query("""
          SELECT r
          FROM RegistroAsistencia r
          WHERE r.empleado.id = :empleadoId
          AND r.fechaHora >= :inicio
          AND r.fechaHora < :fin
          """)
    List<RegistroAsistencia> findByEmpleadoIdAndFecha(
          @Param("empleadoId") Long empleadoId,
          @Param("inicio") LocalDateTime inicio,
          @Param("fin") LocalDateTime fin);
    
    // Buscar última marcación de un empleado
    Optional<RegistroAsistencia> findTopByEmpleadoIdOrderByFechaHoraDesc(Long empleadoId);
    
    // Buscar por estado
    List<RegistroAsistencia> findByEstado(String estado);
    long countByEstado(String estado);
    
    // Contar asistencias por empleado en un rango de fechas
    long countByEmpleadoIdAndFechaHoraBetween(Long empleadoId, LocalDateTime inicio, LocalDateTime fin);

    @Query("""
       SELECT r
       FROM RegistroAsistencia r
       JOIN FETCH r.empleado e
       WHERE r.fechaHora >= :inicio
       AND r.fechaHora < :fin
       ORDER BY r.fechaHora DESC
       """)
    List<RegistroAsistencia> asistenciasHoy(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    @Query("""
       SELECT r
       FROM RegistroAsistencia r
       WHERE r.estado IN ('OBSERVADO', 'RECHAZADO')
       ORDER BY r.fechaHora DESC
       """)
    List<RegistroAsistencia> incidenciasAsistencia();

    @Query("""
       SELECT COUNT(r)
       FROM RegistroAsistencia r
       WHERE r.empleado.id = :empleadoId
       AND r.fechaHora BETWEEN :inicio AND :fin
       """)
    Long contarAsistenciasMensuales(
            @Param("empleadoId") Long empleadoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    @Query("""
       SELECT r.empleado.id, r.empleado.nombres, r.empleado.apellidos, COUNT(r)
       FROM RegistroAsistencia r
       WHERE r.estado = 'OBSERVADO'
       GROUP BY r.empleado.id, r.empleado.nombres, r.empleado.apellidos
       ORDER BY COUNT(r) DESC
       """)
    List<Object[]> rankingTardanzas();

    @Query("""
       SELECT COUNT(r) > 0
       FROM RegistroAsistencia r
       WHERE r.empleado.id = :empleadoId
          AND r.fechaHora >= :inicio
          AND r.fechaHora < :fin
       AND r.tipoMarcacion = :tipo
       """)
    boolean yaMarcoHoy(
            @Param("empleadoId") Long empleadoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("tipo") String tipo);

    @Query("""
       SELECT r
       FROM RegistroAsistencia r
       JOIN FETCH r.empleado
       LEFT JOIN FETCH r.dispositivo
       ORDER BY r.fechaHora DESC
       """)
    List<RegistroAsistencia> listarCompleto();

    // ============================================
    // ✅ NUEVAS QUERIES PARA PATRONES Y MÉTRICAS
    // ============================================

    /**
     * Encuentra tardanzas consecutivas de un empleado (más de 2 días seguidos con tardanza)
     */
    @Query(value = """
       SELECT COUNT(*) FROM (
           SELECT r.fecha_hora::date as dia,
                  r.minutos_tardanza,
                  LAG(r.fecha_hora::date) OVER (ORDER BY r.fecha_hora::date) as dia_anterior,
                  CASE WHEN r.minutos_tardanza > 0 THEN 1 ELSE 0 END as es_tardanza
           FROM registros_asistencia r
           WHERE r.empleado_id = :empleadoId
           AND r.tipo_marcacion = 'ENTRADA'
           AND r.fecha_hora >= :inicio
           AND r.fecha_hora <= :fin
       ) sub
       WHERE sub.es_tardanza = 1
       AND sub.dia_anterior = sub.dia - INTERVAL '1 day'
       """, nativeQuery = true)
    Long countTardanzasConsecutivas(
            @Param("empleadoId") Long empleadoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Cuenta faltas mensuales de un empleado (días laborables sin registro)
     */
    @Query("""
       SELECT COUNT(r)
       FROM RegistroAsistencia r
       WHERE r.empleado.id = :empleadoId
       AND r.esFalta = true
       AND r.fechaHora BETWEEN :inicio AND :fin
       """)
    Long countFaltasMensuales(
            @Param("empleadoId") Long empleadoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Encuentra patrones semanales de tardanza (mismo día de la semana con tardanza)
     */
    @Query(value = """
       SELECT EXTRACT(DOW FROM r.fecha_hora) as dia_semana,
              COUNT(*) as cantidad_tardanzas,
              AVG(r.minutos_tardanza) as promedio_tardanza
       FROM registros_asistencia r
       WHERE r.empleado_id = :empleadoId
       AND r.tipo_marcacion = 'ENTRADA'
       AND r.minutos_tardanza > 0
       AND r.fecha_hora >= :inicio
       AND r.fecha_hora <= :fin
       GROUP BY EXTRACT(DOW FROM r.fecha_hora)
       HAVING COUNT(*) >= 3
       ORDER BY cantidad_tardanzas DESC
       """, nativeQuery = true)
    List<Object[]> findPatronesSemanales(
            @Param("empleadoId") Long empleadoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Empleados activos que no marcaron entrada hoy (para detectar faltas)
     */
    @Query(value = """
       SELECT e.id, e.nombres, e.apellidos, e.dni, e.hora_entrada
       FROM empleados e
       WHERE e.activo = true
       AND e.id NOT IN (
           SELECT r.empleado_id
           FROM registros_asistencia r
           WHERE r.tipo_marcacion = 'ENTRADA'
           AND r.fecha_hora >= :inicio
           AND r.fecha_hora < :fin
           AND r.estado != 'RECHAZADO'
       )
       """, nativeQuery = true)
    List<Object[]> empleadosSinMarcarHoy(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Obtiene todas las entradas con tardanza de un empleado en un período
     */
    @Query("""
       SELECT r
       FROM RegistroAsistencia r
       WHERE r.empleado.id = :empleadoId
       AND r.tipoMarcacion = 'ENTRADA'
       AND r.minutosTardanza > 0
       AND r.fechaHora BETWEEN :inicio AND :fin
       ORDER BY r.fechaHora DESC
       """)
    List<RegistroAsistencia> findTardanzasByEmpleado(
            @Param("empleadoId") Long empleadoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Cuenta total de tardanzas de un empleado en un período
     */
    @Query("""
       SELECT COUNT(r)
       FROM RegistroAsistencia r
       WHERE r.empleado.id = :empleadoId
       AND r.tipoMarcacion = 'ENTRADA'
       AND r.minutosTardanza > 0
       AND r.fechaHora BETWEEN :inicio AND :fin
       """)
    Long countTardanzasByEmpleado(
            @Param("empleadoId") Long empleadoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Obtiene entradas puntuales vs tardanzas para calcular índice de puntualidad
     */
    @Query("""
       SELECT 
           COUNT(CASE WHEN r.minutosTardanza = 0 OR r.minutosTardanza IS NULL THEN 1 END) as puntuales,
           COUNT(CASE WHEN r.minutosTardanza > 0 THEN 1 END) as tardanzas,
           COUNT(*) as total
       FROM RegistroAsistencia r
       WHERE r.empleado.id = :empleadoId
       AND r.tipoMarcacion = 'ENTRADA'
       AND r.fechaHora BETWEEN :inicio AND :fin
       AND r.estado != 'RECHAZADO'
       """)
    List<Object[]> obtenerEstadisticasPuntualidad(
            @Param("empleadoId") Long empleadoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);
}