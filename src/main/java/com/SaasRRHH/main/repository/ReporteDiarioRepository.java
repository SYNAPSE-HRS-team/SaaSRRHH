package com.SaasRRHH.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SaasRRHH.main.model.ReporteDiario;

@Repository
public interface ReporteDiarioRepository extends JpaRepository<ReporteDiario, Long> {
}
