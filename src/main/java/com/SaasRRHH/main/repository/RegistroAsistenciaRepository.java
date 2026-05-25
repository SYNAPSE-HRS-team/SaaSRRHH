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
    Optional<RegistroAsistencia>
    findTopByEmpleadoIdOrderByFechaHoraDesc(Long empleadoId);
    
    // Buscar por estado
    List<RegistroAsistencia> findByEstado(String estado);
    
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
       SELECT r.empleado.id, r.empleado.nombres, COUNT(r)
       FROM RegistroAsistencia r
       WHERE r.estado = 'OBSERVADO'
       GROUP BY r.empleado.id, r.empleado.nombres
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





}