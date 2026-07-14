package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.MetricaBurnoutResponseDTO;
import java.util.List;

public interface MetricaBurnoutService {

    // ============================================
    // CRUD BÁSICO
    // ============================================
    
    List<MetricaBurnoutResponseDTO> listar();

    MetricaBurnoutResponseDTO obtenerPorId(Long id);

    List<MetricaBurnoutResponseDTO> buscarPorEmpleado(Long empleadoId);

    void eliminar(Long id);

    // ============================================
    // CÁLCULO AUTOMÁTICO
    // ============================================
    
    /**
     * Calcula la métrica de burnout para un empleado específico
     */
    MetricaBurnoutResponseDTO calcularMetrica(Long empleadoId);

    /**
     * Recalcula métricas para todos los empleados activos
     */
    List<MetricaBurnoutResponseDTO> recalcularTodas();

    /**
     * Obtiene el historial completo de métricas de un empleado
     */
    List<MetricaBurnoutResponseDTO> obtenerHistorialCompleto(Long empleadoId);

    /**
     * Obtiene el último nivel de riesgo de un empleado
     */
    String obtenerUltimoNivelRiesgo(Long empleadoId);
}