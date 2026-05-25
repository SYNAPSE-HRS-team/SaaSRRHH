package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.EncuestaBienestarRequestDTO;
import com.SaasRRHH.main.DTO.EncuestaBienestarResponseDTO;

import java.util.List;

public interface EncuestaBienestarService {

    List<EncuestaBienestarResponseDTO> listar();

    EncuestaBienestarResponseDTO guardar(EncuestaBienestarRequestDTO encuesta);

    EncuestaBienestarResponseDTO obtenerPorId(Long id);

    EncuestaBienestarResponseDTO actualizar(Long id, EncuestaBienestarRequestDTO data);

    void eliminar(Long id);
}