package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.MetricaBurnout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MetricaBurnoutRepository
        extends JpaRepository<MetricaBurnout,Long> {

    List<MetricaBurnout> findByEmpleadoId(Long empleadoId);

} 