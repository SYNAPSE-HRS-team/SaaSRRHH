package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.entity.TareaAsignada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TareaAsignadaRepository extends JpaRepository<TareaAsignada, Long> {
}