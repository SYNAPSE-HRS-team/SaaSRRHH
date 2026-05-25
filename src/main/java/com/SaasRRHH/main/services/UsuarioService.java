package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.UsuarioRequestDTO;
import com.SaasRRHH.main.DTO.UsuarioResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface UsuarioService {

    List<UsuarioResponseDTO> listar();

    UsuarioResponseDTO buscarPorId(Long id);

    UsuarioResponseDTO guardar(UsuarioRequestDTO dto);

    void eliminar(Long id);

    UsuarioResponseDTO buscarPorEmail(String email);

    UsuarioResponseDTO actualizarUltimoAcceso(Long id);

    boolean existsByEmail(String email);

    // =========================
    // CONSULTAS JPQL
    // =========================

    List<UsuarioResponseDTO> listarUsuariosActivos();

    List<UsuarioResponseDTO> buscarPorRol(String rol);

    List<UsuarioResponseDTO> usuariosConAccesoReciente(
            LocalDateTime fecha);

    List<Object[]> contarUsuariosPorRol();
}