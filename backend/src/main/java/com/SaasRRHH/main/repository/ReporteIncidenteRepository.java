package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.ReporteIncidente;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReporteIncidenteRepository extends JpaRepository<ReporteIncidente, Long> {



    @Query("""
        SELECT r FROM ReporteIncidente r
        JOIN FETCH r.empleado e JOIN FETCH e.usuario
        LEFT JOIN FETCH r.supervisor s LEFT JOIN FETCH s.usuario
        LEFT JOIN FETCH r.tarea t LEFT JOIN FETCH t.empleado te LEFT JOIN FETCH te.usuario
        LEFT JOIN FETCH r.area
    """)
    List<ReporteIncidente> findAllWithRelaciones();


    @Query("""
        SELECT r FROM ReporteIncidente r
        JOIN FETCH r.empleado e JOIN FETCH e.usuario
        LEFT JOIN FETCH r.supervisor s LEFT JOIN FETCH s.usuario
        LEFT JOIN FETCH r.tarea t LEFT JOIN FETCH t.empleado te LEFT JOIN FETCH te.usuario
        LEFT JOIN FETCH r.area
        WHERE r.id = :id
    """)
    Optional<ReporteIncidente> findByIdWithRelaciones(@Param("id") Long id);

    @Query("""
        SELECT r FROM ReporteIncidente r
        WHERE r.fechaIncidente BETWEEN :inicio AND :fin
        ORDER BY r.fechaIncidente DESC
    """)
    List<ReporteIncidente> findByRangoFechas(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );


    @Query("""
        SELECT r FROM ReporteIncidente r
        JOIN FETCH r.empleado e JOIN FETCH e.usuario
        WHERE e.id = :empleadoId
        ORDER BY r.fechaIncidente DESC
    """)
    List<ReporteIncidente> findByEmpleado(@Param("empleadoId") Long empleadoId);


    List<ReporteIncidente> findByNivelRiesgo(ReporteIncidente.NivelRiesgo nivelRiesgo);


    List<ReporteIncidente> findByEstado(ReporteIncidente.EstadoIncidente estado);


    @Query("""
        SELECT r FROM ReporteIncidente r
        WHERE r.nivelRiesgo IN ('ALTO', 'CRITICO')
        ORDER BY r.fechaIncidente DESC
    """)
    List<ReporteIncidente> incidentesCriticos();


    @Query("""
        SELECT r FROM ReporteIncidente r
        WHERE FUNCTION('DATE', r.fechaIncidente) = CURRENT_DATE
    """)
    List<ReporteIncidente> incidentesDeHoy();



    @Query("""
        SELECT r.empleado.id, COUNT(r)
        FROM ReporteIncidente r
        GROUP BY r.empleado.id
        ORDER BY COUNT(r) DESC
    """)
    List<Object[]> incidentesPorEmpleado();


    @Query("""
        SELECT r.nivelRiesgo, COUNT(r)
        FROM ReporteIncidente r
        GROUP BY r.nivelRiesgo
    """)
    List<Object[]> incidentesPorRiesgo();


    @Query("""
        SELECT r.area.id, COUNT(r)
        FROM ReporteIncidente r
        GROUP BY r.area.id
    """)
    List<Object[]> incidentesPorArea();


    @Query("""
        SELECT r.supervisor.id, COUNT(r)
        FROM ReporteIncidente r
        GROUP BY r.supervisor.id
        ORDER BY COUNT(r) DESC
    """)
    List<Object[]> incidentesPorSupervisor();


    @Query("""
        SELECT r FROM ReporteIncidente r
        LEFT JOIN FETCH r.empleado e LEFT JOIN FETCH e.usuario
        LEFT JOIN FETCH r.area
        WHERE r.nivelRiesgo = 'CRITICO'
    """)
    List<ReporteIncidente> incidentesCriticosConDetalle();
}