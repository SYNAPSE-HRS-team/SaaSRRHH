package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.ReporteDiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReporteDiarioRepository extends JpaRepository<ReporteDiario, Long> {

    // =========================
    // 📋 CONSULTAS PRINCIPALES (OPTIMIZADAS)
    // =========================

    @Query("""
        SELECT r FROM ReporteDiario r
        JOIN FETCH r.tarea t
        JOIN FETCH r.empleado e
        JOIN FETCH t.empleado te
        JOIN FETCH t.supervisor ts
        JOIN FETCH t.area a
    """)
    List<ReporteDiario> findAllWithRelaciones();


    @Query("""
        SELECT r FROM ReporteDiario r
        JOIN FETCH r.tarea t
        JOIN FETCH r.empleado e
        JOIN FETCH t.empleado te
        JOIN FETCH t.supervisor ts
        JOIN FETCH t.area a
        WHERE r.id = :id
    """)
    Optional<ReporteDiario> findByIdWithRelaciones(@Param("id") Long id);




    //  Reportes por rango de fechas
    @Query("""
        SELECT r FROM ReporteDiario r
        JOIN FETCH r.tarea t
        JOIN FETCH r.empleado e
        WHERE r.fechaReporte BETWEEN :inicio AND :fin
        ORDER BY r.fechaReporte DESC
    """)
    List<ReporteDiario> findByRangoFechas(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );


    //  Reportes por estado (EN PROGRESO, COMPLETADO, etc.)
    List<ReporteDiario> findByEstado(ReporteDiario.EstadoReporte estado);



    @Query("""
        SELECT r FROM ReporteDiario r
        JOIN FETCH r.empleado e
        JOIN FETCH r.tarea t
        WHERE e.id = :empleadoId
        ORDER BY r.fechaReporte DESC
    """)
    List<ReporteDiario> findByEmpleado(@Param("empleadoId") Long empleadoId);


    //
    @Query("""
        SELECT r FROM ReporteDiario r
        JOIN FETCH r.tarea t
        WHERE t.id = :tareaId
    """)
    List<ReporteDiario> findByTarea(@Param("tareaId") Long tareaId);


    // =========================
    // ANALÍTICA
    // =========================

    // 👷 Cantidad de reportes por empleado
    @Query("""
        SELECT r.empleado.id, COUNT(r)
        FROM ReporteDiario r
        GROUP BY r.empleado.id
        ORDER BY COUNT(r) DESC
    """)
    List<Object[]> reportesPorEmpleado();


    //  Promedio de avance por tarea
    @Query("""
        SELECT r.tarea.id, AVG(r.porcentajeAvance)
        FROM ReporteDiario r
        GROUP BY r.tarea.id
    """)
    List<Object[]> avancePromedioPorTarea();


    // ⚠Reportes con bajo avance (<50%)
    @Query("""
        SELECT r FROM ReporteDiario r
        WHERE r.porcentajeAvance < 50
    """)
    List<ReporteDiario> reportesBajoAvance();


    // Reportes de hoy
    @Query("""
        SELECT r FROM ReporteDiario r
        WHERE FUNCTION('DATE', r.fechaReporte) = CURRENT_DATE
    """)
    List<ReporteDiario> reportesDeHoy();
}
