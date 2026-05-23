package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.ReporteDiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteDiarioRepository extends JpaRepository<ReporteDiario, Long> {
}
