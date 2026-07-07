package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.DocumentoPrivadoRequestDTO;
import com.SaasRRHH.main.DTO.DocumentoPrivadoResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface DocumentoPrivadoService {

    List<DocumentoPrivadoResponseDTO> listar();

    DocumentoPrivadoResponseDTO buscarPorId(Long id);

    DocumentoPrivadoResponseDTO guardar(
            DocumentoPrivadoRequestDTO dto);

    DocumentoPrivadoResponseDTO actualizar(
            Long id,
            DocumentoPrivadoRequestDTO dto);

    void eliminar(Long id);

    // ===================================
    // CONSULTAS
    // ===================================

    List<DocumentoPrivadoResponseDTO>
    listarActivos();

    List<DocumentoPrivadoResponseDTO>
    buscarPorEmpleado(Long empleadoId);

    List<DocumentoPrivadoResponseDTO>
    buscarPorTipo(Long tipoId);

    List<DocumentoPrivadoResponseDTO>
    listarActivosConRelaciones();

    List<DocumentoPrivadoResponseDTO>
    documentosVencidos();

    List<DocumentoPrivadoResponseDTO>
    documentosPorVencer(
            LocalDate fechaLimite);

    List<Object[]>
    contarDocumentosPorTipo();

    List<Object[]>
    empleadosConMasDocumentos();

    List<DocumentoPrivadoResponseDTO> buscarPorFechaEmision(LocalDate fechaEmision);

}