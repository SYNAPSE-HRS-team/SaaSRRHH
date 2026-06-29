package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.EncuestaBienestarRequestDTO;
import com.SaasRRHH.main.DTO.EncuestaBienestarResponseDTO;

import java.util.List;
import java.time.LocalDate;
import com.SaasRRHH.main.DTO.ResumenBienestarDTO;

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
}