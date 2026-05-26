package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.ReporteIncidenteRequestDTO;
import com.SaasRRHH.main.DTO.ReporteIncidenteResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ReporteIncidenteService {

    // =========================
    // 📋 CRUD
    // =========================

    List<ReporteIncidenteResponseDTO> listar();

    ReporteIncidenteResponseDTO guardar(ReporteIncidenteRequestDTO dto);

    ReporteIncidenteResponseDTO obtenerPorId(Long id);

    ReporteIncidenteResponseDTO actualizar(Long id, ReporteIncidenteRequestDTO dto);

    void eliminar(Long id);




    List<ReporteIncidenteResponseDTO> listarConRelaciones();

    List<ReporteIncidenteResponseDTO> listarPorEmpleado(Long empleadoId);

    List<ReporteIncidenteResponseDTO> buscarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);

    List<ReporteIncidenteResponseDTO> listarPorNivelRiesgo(String nivelRiesgo);

    List<ReporteIncidenteResponseDTO> listarPorEstado(String estado);

    List<ReporteIncidenteResponseDTO> incidentesCriticos();

    List<ReporteIncidenteResponseDTO> incidentesDeHoy();

    List<ReporteIncidenteResponseDTO> incidentesCriticosConDetalle();



    List<Object[]> incidentesPorEmpleado();

    List<Object[]> incidentesPorRiesgo();

    List<Object[]> incidentesPorArea();

    List<Object[]> incidentesPorSupervisor();
}