package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.DocumentoPrivado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoPrivadoRepository extends JpaRepository<DocumentoPrivado, Long> {
}