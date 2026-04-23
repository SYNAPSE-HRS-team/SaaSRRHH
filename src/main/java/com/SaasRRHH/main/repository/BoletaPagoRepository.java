package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.BoletaPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoletaPagoRepository extends JpaRepository<BoletaPago, Long> {
}