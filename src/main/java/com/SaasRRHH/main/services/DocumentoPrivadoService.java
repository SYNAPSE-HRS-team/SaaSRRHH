package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.DocumentoPrivadoRequestDTO;
import com.SaasRRHH.main.DTO.DocumentoPrivadoResponseDTO;
import com.SaasRRHH.main.model.DocumentoPrivado;

import java.util.List;
import java.util.Optional;

public interface DocumentoPrivadoService {

    List<DocumentoPrivadoResponseDTO> listar();

    DocumentoPrivadoResponseDTO buscarPorId(Long id);

    DocumentoPrivadoResponseDTO guardar(DocumentoPrivadoRequestDTO dto);

    DocumentoPrivadoResponseDTO actualizar(Long id, DocumentoPrivadoRequestDTO dto);

    void eliminar(Long id);
}