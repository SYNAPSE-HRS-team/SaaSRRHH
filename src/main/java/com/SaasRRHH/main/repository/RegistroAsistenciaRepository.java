package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.entity.RegistroAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroAsistenciaRepository extends JpaRepository<RegistroAsistencia, Long> {
}