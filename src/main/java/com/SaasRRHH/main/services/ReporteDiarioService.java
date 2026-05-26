package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.ReporteDiarioRequestDTO;
import com.SaasRRHH.main.DTO.ReporteDiarioResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ReporteDiarioService {

    // =========================
    // 📋 CRUD
    // =========================

    List<ReporteDiarioResponseDTO> listar();

    ReporteDiarioResponseDTO buscarPorId(Long id);

    ReporteDiarioResponseDTO guardar(ReporteDiarioRequestDTO dto);

    ReporteDiarioResponseDTO actualizar(Long id, ReporteDiarioRequestDTO dto);

    void eliminar(Long id);

    // =========================
    // 📊 CONSULTAS JPQL
    // =========================

    List<ReporteDiarioResponseDTO> buscarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);

    List<ReporteDiarioResponseDTO> buscarPorEmpleado(Long empleadoId);

    List<ReporteDiarioResponseDTO> buscarPorTarea(Long tareaId);

    List<ReporteDiarioResponseDTO> reportesBajoAvance();

    List<ReporteDiarioResponseDTO> reportesDeHoy();

    List<ReporteDiarioResponseDTO> listarPorEstado(String estado);

    // =========================
    // 📈 ANALÍTICA (GROUP BY)
    // =========================

    List<Object[]> reportesPorEmpleado();

    List<Object[]> avancePromedioPorTarea();
}
