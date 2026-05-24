package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.EmpleadoRequestDTO;
import com.SaasRRHH.main.DTO.EmpleadoResponseDTO;

import java.util.List;

public interface EmpleadoService {

    List<EmpleadoResponseDTO> listar();

    EmpleadoResponseDTO buscarPorId(Long id);

    EmpleadoResponseDTO buscarPorDni(String dni);

    List<EmpleadoResponseDTO> listarActivos();

    EmpleadoResponseDTO buscarPorUsuarioId(Long userId);

    EmpleadoResponseDTO guardar(EmpleadoRequestDTO dto);

    void eliminar(Long id);


}
