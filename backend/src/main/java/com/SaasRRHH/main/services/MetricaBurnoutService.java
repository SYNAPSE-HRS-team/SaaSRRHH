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
    //  CÁLCULO AUTOMÁTICO
    // ============================================
    
    MetricaBurnoutResponseDTO calcularMetrica(Long empleadoId);

    List<MetricaBurnoutResponseDTO> recalcularTodas();

  
    List<MetricaBurnoutResponseDTO> obtenerHistorialCompleto(Long empleadoId);

    String obtenerUltimoNivelRiesgo(Long empleadoId);
}