package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.DocumentoPrivadoRequestDTO;
import com.SaasRRHH.main.DTO.DocumentoPrivadoResponseDTO;

import java.util.List;

public interface DocumentoPrivadoService {

    List<DocumentoPrivadoResponseDTO> listar();

    DocumentoPrivadoResponseDTO buscarPorId(Long id);

    DocumentoPrivadoResponseDTO guardar(DocumentoPrivadoRequestDTO dto);

    DocumentoPrivadoResponseDTO actualizar(Long id, DocumentoPrivadoRequestDTO dto);

    void eliminar(Long id);
}