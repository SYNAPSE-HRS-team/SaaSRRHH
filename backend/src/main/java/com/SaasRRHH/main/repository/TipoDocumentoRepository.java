package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.TipoDocumento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TipoDocumentoRepository
        extends JpaRepository<TipoDocumento, Long> {

    // ==============================
    // CONSULTAS DERIVADAS
    // ==============================

    Optional<TipoDocumento> findByNombre(
            String nombre);

    List<TipoDocumento>
    findByObligatorioTrue();

    List<TipoDocumento>
    findByRequiereRenovacionTrue();

    // ==============================
    // JPQL
    // ==============================

    @Query("""
           SELECT t
           FROM TipoDocumento t
           WHERE t.diasVigencia IS NOT NULL
           ORDER BY t.diasVigencia ASC
           """)
    List<TipoDocumento>
    listarPorVigencia();

    @Query("""
           SELECT COUNT(t)
           FROM TipoDocumento t
           WHERE t.obligatorio = true
           """)
    Long contarObligatorios();
}