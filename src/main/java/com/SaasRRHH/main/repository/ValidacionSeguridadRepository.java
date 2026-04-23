package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.ValidacionSeguridad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValidacionSeguridadRepository extends JpaRepository<ValidacionSeguridad, Long> {
}
