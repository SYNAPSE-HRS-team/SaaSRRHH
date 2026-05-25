package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.RegistroAsistenciaRequestDTO;
import com.SaasRRHH.main.DTO.RegistroAsistenciaResponseDTO;
import java.time.LocalDate;
import java.util.List;

public interface RegistroAsistenciaService {

    List<RegistroAsistenciaResponseDTO> listar();

    RegistroAsistenciaResponseDTO buscarPorId(Long id);

    RegistroAsistenciaResponseDTO guardar(RegistroAsistenciaRequestDTO registroAsistencia);

    RegistroAsistenciaResponseDTO registrarEntrada(Long empleadoId, String metodo);

    RegistroAsistenciaResponseDTO registrarSalida(Long empleadoId, String metodo);

    void eliminar(Long id);

    List<RegistroAsistenciaResponseDTO> buscarPorEmpleado(Long empleadoId);

    List<RegistroAsistenciaResponseDTO> buscarPorEmpleadoYFecha(Long empleadoId, LocalDate fecha);

    List<RegistroAsistenciaResponseDTO> buscarPorEstado(String estado);
}