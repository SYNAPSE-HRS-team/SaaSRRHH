package com.SaasRRHH.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SaasRRHH.main.model.ValidacionSeguridad;

@Repository
public interface ValidacionSeguridadRepository extends JpaRepository<ValidacionSeguridad, Long> {
}
