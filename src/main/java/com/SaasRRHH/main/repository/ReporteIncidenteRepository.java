package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.ReporteIncidente;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteIncidenteRepository extends JpaRepository<ReporteIncidente, Long> {
    @Query("SELECT r FROM ReporteIncidente r " +
            "JOIN FETCH r.empleado e JOIN FETCH e.usuario " +
            "LEFT JOIN FETCH r.supervisor s LEFT JOIN FETCH s.usuario " +
            "LEFT JOIN FETCH r.tarea t LEFT JOIN FETCH t.empleado te LEFT JOIN FETCH te.usuario " +
            "LEFT JOIN FETCH r.area")
    List<ReporteIncidente> findAllWithRelaciones();

    @Query("SELECT r FROM ReporteIncidente r " +
            "JOIN FETCH r.empleado e JOIN FETCH e.usuario " +
            "LEFT JOIN FETCH r.supervisor s LEFT JOIN FETCH s.usuario " +
            "LEFT JOIN FETCH r.tarea t LEFT JOIN FETCH t.empleado te LEFT JOIN FETCH te.usuario " +
            "LEFT JOIN FETCH r.area " +
            "WHERE r.id = :id")
    Optional<ReporteIncidente> findByIdWithRelaciones(@Param("id") Long id);
}