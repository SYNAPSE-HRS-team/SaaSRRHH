package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.ValidacionSeguridadRequestDTO;
import com.SaasRRHH.main.DTO.ValidacionSeguridadResponseDTO;

import java.util.List;

public interface ValidacionSeguridadService {

    // =====================================
    // CRUD
    // =====================================

    List<ValidacionSeguridadResponseDTO> listar();

    ValidacionSeguridadResponseDTO buscarPorId(Long id);

    ValidacionSeguridadResponseDTO guardar(
            ValidacionSeguridadRequestDTO dto);

    ValidacionSeguridadResponseDTO actualizar(
            Long id,
            ValidacionSeguridadRequestDTO dto);

    void eliminar(Long id);

    // =====================================
    // CONSULTAS
    // =====================================

    List<ValidacionSeguridadResponseDTO>
    buscarPorTotpValido(Boolean valido);

    List<ValidacionSeguridadResponseDTO>
    recientes();

    List<ValidacionSeguridadResponseDTO>
    buscarPorEmpleado(Long empleadoId);

    List<ValidacionSeguridadResponseDTO>
    intentosFallidos();

}