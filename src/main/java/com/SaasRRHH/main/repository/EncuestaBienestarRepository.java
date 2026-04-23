package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.Encuestabienestar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface EncuestaBienestarRepository extends JpaRepository<Encuestabienestar, Long> {
}