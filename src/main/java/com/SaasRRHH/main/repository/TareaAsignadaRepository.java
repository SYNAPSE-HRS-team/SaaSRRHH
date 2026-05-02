package com.SaasRRHH.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SaasRRHH.main.model.TareaAsignada;

@Repository
public interface TareaAsignadaRepository extends JpaRepository<TareaAsignada, Long> {
}