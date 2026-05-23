package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.DocumentoPrivado;

import java.util.List;
import java.util.Optional;

public interface DocumentoPrivadoService {

    List<DocumentoPrivado> listar();

    Optional<DocumentoPrivado> buscarPorId(Long id);

    DocumentoPrivado guardar(DocumentoPrivado documentoPrivado);

    DocumentoPrivado actualizar(Long id, DocumentoPrivado documentoPrivado);

    void eliminar(Long id);
}