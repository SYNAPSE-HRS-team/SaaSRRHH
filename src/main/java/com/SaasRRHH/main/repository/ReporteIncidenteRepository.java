package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.ReporteIncidente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteIncidenteRepository extends JpaRepository<ReporteIncidente, Long> {
}