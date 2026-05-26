package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.FamiliarRequestDTO;
import com.SaasRRHH.main.DTO.FamiliarResponseDTO;
import com.SaasRRHH.main.model.Familiar;

import java.util.List;

public interface FamiliarService {

    List<FamiliarResponseDTO> listar();

    FamiliarResponseDTO buscarPorId(Long id);

    FamiliarResponseDTO guardar(
            FamiliarRequestDTO dto);

    FamiliarResponseDTO actualizar(
            Long id,
            FamiliarRequestDTO dto);

    void eliminar(Long id);

    List<FamiliarResponseDTO>
    findByEmpleadoId(Long empleadoId);

    // ===================================
    // CONSULTAS
    // ===================================

    List<FamiliarResponseDTO>
    listarActivos();

    List<FamiliarResponseDTO>
    buscarPorParentesco(
            Familiar.Parentesco parentesco);

    List<FamiliarResponseDTO>
    familiaresQueEstudian();

    List<Object[]>
    contarPorParentesco();
}