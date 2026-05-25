package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.DispositivoAutorizado;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DispositivoAutorizadoRepository extends JpaRepository<DispositivoAutorizado, Long> {
    @Query("SELECT d FROM DispositivoAutorizado d JOIN FETCH d.usuario")
    List<DispositivoAutorizado> findAllWithUsuario();
}
