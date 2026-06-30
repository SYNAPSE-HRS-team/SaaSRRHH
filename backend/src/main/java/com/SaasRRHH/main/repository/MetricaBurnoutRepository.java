package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.MetricaBurnout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MetricaBurnoutRepository
        extends JpaRepository<MetricaBurnout, Long> {

    @Query("SELECT m FROM MetricaBurnout m JOIN FETCH m.empleado e JOIN FETCH e.usuario")
    List<MetricaBurnout> findAllWithRelaciones();

    @Query("SELECT m FROM MetricaBurnout m JOIN FETCH m.empleado e JOIN FETCH e.usuario WHERE m.id = :id")
    Optional<MetricaBurnout> findByIdWithRelaciones(@Param("id") Long id);

    @Query("SELECT m FROM MetricaBurnout m JOIN FETCH m.empleado e JOIN FETCH e.usuario WHERE e.id = :empleadoId")
    List<MetricaBurnout> findByEmpleadoId(@Param("empleadoId") Long empleadoId);

}