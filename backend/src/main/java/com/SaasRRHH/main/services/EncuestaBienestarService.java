package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.EncuestaBienestarRequestDTO;
import com.SaasRRHH.main.DTO.EncuestaBienestarResponseDTO;
import com.SaasRRHH.main.DTO.ResumenBienestarDTO;
import java.time.LocalDate;
import java.util.List;

public interface EncuestaBienestarService {

    List<EncuestaBienestarResponseDTO> listar();

    EncuestaBienestarResponseDTO guardar(EncuestaBienestarRequestDTO encuesta);

    EncuestaBienestarResponseDTO obtenerPorId(Long id);

    EncuestaBienestarResponseDTO actualizar(Long id, EncuestaBienestarRequestDTO data);

    void eliminar(Long id);

    List<EncuestaBienestarResponseDTO> obtenerHistorialEmpleado(Long empleadoId);

    List<EncuestaBienestarResponseDTO> obtenerPorRangoFechas(LocalDate inicio, LocalDate fin);

    List<Long> obtenerEmpleadosEnRiesgo();

    ResumenBienestarDTO obtenerResumenMensual(LocalDate inicio, LocalDate fin);

    // ============================================
    // ✅ NUEVOS MÉTODOS DE INTEGRACIÓN CON BURNOUT
    // ============================================

    /**
     * Evalúa el riesgo de burnout basado en las encuestas de bienestar
     * @return BAJO, MEDIO, ALTO, SIN_DATOS
     */
    String evaluarRiesgoPorEncuestas(Long empleadoId);

    /**
     * Compara el riesgo detectado en encuestas con el riesgo de métricas de burnout
     * @return String con el resultado de la comparación
     */
    String compararConBurnout(Long empleadoId);
}