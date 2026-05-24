package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.EmpleadoRequestDTO;
import com.SaasRRHH.main.DTO.EmpleadoResponseDTO;
import com.SaasRRHH.main.model.Empleado;

import java.util.List;
import java.util.Optional;

public interface EmpleadoService {

    List<EmpleadoResponseDTO> listar();

    EmpleadoResponseDTO buscarPorId(Long id);

    EmpleadoResponseDTO buscarPorDni(String dni);

    List<EmpleadoResponseDTO> listarActivos();

    EmpleadoResponseDTO buscarPorUsuarioId(Long userId);

    EmpleadoResponseDTO guardar(EmpleadoRequestDTO dto);

    void eliminar(Long id);


}
