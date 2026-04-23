package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.MetricaBurnout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MetricaBurnoutRepository
        extends JpaRepository<MetricaBurnout,Long> {

    List<MetricaBurnout> findByEmpleadoId(Long empleadoId);

} 