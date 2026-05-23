package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.FamiliarDTO;

import java.util.List;

public interface FamiliarService {

    List<FamiliarDTO> listar();

    FamiliarDTO buscarPorId(Long id);

    FamiliarDTO guardar(FamiliarDTO dto);

    FamiliarDTO actualizar(Long id, FamiliarDTO dto);

    void eliminar(Long id);

    List<FamiliarDTO> findByEmpleadoId(Long empleadoId);
}