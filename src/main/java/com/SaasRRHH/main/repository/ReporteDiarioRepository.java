package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.ReporteDiario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteDiarioRepository extends JpaRepository<ReporteDiario, Long> {
    
    @Query("SELECT r FROM ReporteDiario r JOIN FETCH r.tarea t JOIN FETCH t.empleado te JOIN FETCH te.usuario JOIN FETCH t.supervisor ts JOIN FETCH ts.usuario JOIN FETCH t.area JOIN FETCH r.empleado e JOIN FETCH e.usuario")
    List<ReporteDiario> findAllWithRelaciones();

    @Query("SELECT r FROM ReporteDiario r JOIN FETCH r.tarea t JOIN FETCH t.empleado te JOIN FETCH te.usuario JOIN FETCH t.supervisor ts JOIN FETCH ts.usuario JOIN FETCH t.area JOIN FETCH r.empleado e JOIN FETCH e.usuario WHERE r.id = :id")
    Optional<ReporteDiario> findByIdWithRelaciones(@Param("id") Long id);
}