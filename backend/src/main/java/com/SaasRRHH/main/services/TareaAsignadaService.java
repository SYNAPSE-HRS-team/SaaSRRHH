package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.TareaAsignadaRequestDTO;
import com.SaasRRHH.main.DTO.TareaAsignadaResponseDTO;
import com.SaasRRHH.main.model.TareaAsignada.EstadoTarea;
import java.time.LocalDate;
import java.util.List;

public interface TareaAsignadaService {

    List<TareaAsignadaResponseDTO> listar();

    TareaAsignadaResponseDTO buscarPorId(Long id);

    TareaAsignadaResponseDTO guardar(TareaAsignadaRequestDTO tarea);

    TareaAsignadaResponseDTO actualizar(Long id, TareaAsignadaRequestDTO tarea);

    void eliminar(Long id);

    List<TareaAsignadaResponseDTO> buscarPorEmpleado(Long empleadoId);

    List<TareaAsignadaResponseDTO> buscarPorSupervisor(Long supervisorId);

    List<TareaAsignadaResponseDTO> buscarPorEstado(EstadoTarea estado);

    List<TareaAsignadaResponseDTO> buscarPorEmpleadoYFecha(Long empleadoId, LocalDate fecha);

    TareaAsignadaResponseDTO cambiarEstado(Long id, EstadoTarea nuevoEstado);

    List<TareaAsignadaResponseDTO> buscarPorAreaYEstado(Long areaId, EstadoTarea estado);
}