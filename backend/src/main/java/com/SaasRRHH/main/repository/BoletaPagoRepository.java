package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.BoletaPago;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoletaPagoRepository extends JpaRepository<BoletaPago, Long> {
    @Query("SELECT b FROM BoletaPago b JOIN FETCH b.empleado e JOIN FETCH e.usuario JOIN FETCH b.planilla")
    List<BoletaPago> findAllWithRelaciones();

    @Query("SELECT b FROM BoletaPago b JOIN FETCH b.empleado e JOIN FETCH e.usuario JOIN FETCH b.planilla WHERE b.id = :id")
    Optional<BoletaPago> findByIdWithRelaciones(@Param("id") Long id);

    @Query("SELECT b FROM BoletaPago b JOIN FETCH b.empleado e JOIN FETCH e.usuario JOIN FETCH b.planilla WHERE e.id = :empleadoId")
    List<BoletaPago> findByEmpleadoIdWithRelaciones(@Param("empleadoId") Long empleadoId);
}