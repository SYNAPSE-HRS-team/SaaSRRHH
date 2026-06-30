package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.FeedbackAnonimoRequestDTO;
import com.SaasRRHH.main.DTO.FeedbackAnonimoResponseDTO;
import com.SaasRRHH.main.model.FeedbackAnonimo;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedbackAnonimoService {

    FeedbackAnonimoResponseDTO enviarFeedback(FeedbackAnonimoRequestDTO request);

    List<FeedbackAnonimoResponseDTO> listar();

    List<FeedbackAnonimoResponseDTO> listarPorCategoria(FeedbackAnonimo.CategoriaFeedback categoria);

    List<FeedbackAnonimoResponseDTO> listarPorEstado(FeedbackAnonimo.EstadoFeedback estado);

    List<FeedbackAnonimoResponseDTO> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);

    FeedbackAnonimoResponseDTO cambiarEstado(Long id, FeedbackAnonimo.EstadoFeedback estado);

    void eliminar(Long id);
}
