package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.FeedbackAnonimoRequestDTO;
import com.SaasRRHH.main.DTO.FeedbackAnonimoResponseDTO;
import com.SaasRRHH.main.model.FeedbackAnonimo;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedbackAnonimoService {

    // ============================================
    // MÉTODOS ORIGINALES
    // ============================================

    FeedbackAnonimoResponseDTO enviarFeedback(FeedbackAnonimoRequestDTO request);

    List<FeedbackAnonimoResponseDTO> listar();

    List<FeedbackAnonimoResponseDTO> listarPorCategoria(FeedbackAnonimo.CategoriaFeedback categoria);

    List<FeedbackAnonimoResponseDTO> listarPorEstado(FeedbackAnonimo.EstadoFeedback estado);

    List<FeedbackAnonimoResponseDTO> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);

    FeedbackAnonimoResponseDTO cambiarEstado(Long id, FeedbackAnonimo.EstadoFeedback estado);

    void eliminar(Long id);

    // ============================================
    // ✅ NUEVOS MÉTODOS (DEBEN AGREGARSE A LA INTERFAZ)
    // ============================================

    /**
     * Permite al admin responder un feedback y cambiar su estado
     */
    FeedbackAnonimoResponseDTO responderFeedback(Long id, String respuesta, FeedbackAnonimo.EstadoFeedback estado);

    /**
     * Lista feedback por empleado específico
     */
    List<FeedbackAnonimoResponseDTO> listarPorEmpleado(Long empleadoId);

    /**
     * Lista feedback del empleado autenticado (ve sus propios feedbacks)
     */
    List<FeedbackAnonimoResponseDTO> listarMisFeedbacks(Long empleadoId);

    /**
     * Cuenta feedback pendientes de respuesta
     */
    long contarPendientes();
}