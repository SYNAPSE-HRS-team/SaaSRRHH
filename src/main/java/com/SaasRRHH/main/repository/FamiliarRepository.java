package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.Familiar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamiliarRepository extends JpaRepository<Familiar, Long> {

List<Familiar> findByEmpleadoId(Long empleadoId);
}