package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.EmpleadoRequestDTO;
import com.SaasRRHH.main.DTO.EmpleadoResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface EmpleadoService {

    List<EmpleadoResponseDTO> listar();

    EmpleadoResponseDTO buscarPorId(Long id);

    EmpleadoResponseDTO buscarPorDni(String dni);

    List<EmpleadoResponseDTO> listarActivos();

    EmpleadoResponseDTO buscarPorUsuarioId(Long userId);

    EmpleadoResponseDTO guardar(EmpleadoRequestDTO dto);

    EmpleadoResponseDTO actualizar(Long id, EmpleadoRequestDTO dto);

    void eliminar(Long id);

    // =========================
    // CONSULTAS JPQL
    // =========================
    List<EmpleadoResponseDTO> listarSupervisores();
    List<EmpleadoResponseDTO> buscarPorCargo(String cargo);

    List<EmpleadoResponseDTO> buscarPorCargoYActivo(
            String cargo,
            Boolean activo);

    List<EmpleadoResponseDTO> listarActivosConUsuario();

    List<EmpleadoResponseDTO> contratosVencidos();

    List<EmpleadoResponseDTO> contratosPorVencer(
            LocalDate fechaLimite);

    List<Object[]> contarEmpleadosPorCargo();
    List<EmpleadoResponseDTO> listarTrabajadores();
    List<EmpleadoResponseDTO> listarTrabajadoresByRol();
    List<EmpleadoResponseDTO> listarSupervisoresByRol();
}
