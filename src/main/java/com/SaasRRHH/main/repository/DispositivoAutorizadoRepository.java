package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.DispositivoAutorizado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DispositivoAutorizadoRepository extends JpaRepository<DispositivoAutorizado, Long> {
}
