package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.DispositivoAutorizadoRequestDTO;
import com.SaasRRHH.main.DTO.DispositivoAutorizadoResponseDTO;

import java.util.List;

public interface DispositivoAutorizadoService {

    // ================================
    // CRUD
    // ================================

    List<DispositivoAutorizadoResponseDTO> listarTodo();

    DispositivoAutorizadoResponseDTO buscarPorId(Long id);

    DispositivoAutorizadoResponseDTO guardar(
            DispositivoAutorizadoRequestDTO dto);

    DispositivoAutorizadoResponseDTO actualizar(
            Long id,
            DispositivoAutorizadoRequestDTO dto);

    void eliminar(Long id);

    // ================================
    // CONSULTAS
    // ================================

    List<DispositivoAutorizadoResponseDTO>
    listarActivos();

    List<DispositivoAutorizadoResponseDTO>
    buscarPorUsuario(Long usuarioId);

    boolean existeHardwareRegistrado(
            Long usuarioId,
            String hardwareId);

    List<DispositivoAutorizadoResponseDTO>
    dispositivosRecientes();
}