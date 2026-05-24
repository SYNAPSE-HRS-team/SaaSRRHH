package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.TipoDocumentoRequestDTO;
import com.SaasRRHH.main.DTO.TipoDocumentoResponseDTO;

import java.util.List;

public interface TipoDocumentoService {

    List<TipoDocumentoResponseDTO> listar();

    TipoDocumentoResponseDTO buscarPorId(Long id);

    TipoDocumentoResponseDTO guardar(TipoDocumentoRequestDTO dto);

    TipoDocumentoResponseDTO actualizar(Long id, TipoDocumentoRequestDTO dto);

    void eliminar(Long id);
}