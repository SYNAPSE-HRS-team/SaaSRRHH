package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.TipoDocumento;

import java.util.List;
import java.util.Optional;

public interface TipoDocumentoService {

    List<TipoDocumento> listar();

    Optional<TipoDocumento> buscarPorId(Long id);

    TipoDocumento guardar(TipoDocumento tipoDocumento);

    TipoDocumento actualizar(Long id, TipoDocumento tipoDocumento);

    void eliminar(Long id);
}