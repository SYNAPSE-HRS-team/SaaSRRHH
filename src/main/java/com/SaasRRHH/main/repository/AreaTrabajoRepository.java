package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.entity.AreaTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaTrabajoRepository extends JpaRepository<AreaTrabajo, Long> {
}