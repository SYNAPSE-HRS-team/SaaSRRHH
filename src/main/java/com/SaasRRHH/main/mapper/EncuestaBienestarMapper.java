package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.EncuestaBienestarRequestDTO;
import com.SaasRRHH.main.DTO.EncuestaBienestarResponseDTO;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.Encuestabienestar;

public class EncuestaBienestarMapper {

    public static Encuestabienestar toEntity(EncuestaBienestarRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Encuestabienestar encuesta = new Encuestabienestar();

        if (dto.getEmpleadoId() != null) {
            Empleado empleado = new Empleado();
            empleado.setId(dto.getEmpleadoId());
            encuesta.setEmpleado(empleado);
        }

        encuesta.setFecha(dto.getFecha());
        encuesta.setCargaLaboral(dto.getCargaLaboral());
        encuesta.setApoyoEquipo(dto.getApoyoEquipo());
        encuesta.setProyeccion(dto.getProyeccion());
        return encuesta;
    }

    public static EncuestaBienestarResponseDTO toDTO(Encuestabienestar encuesta) {
        if (encuesta == null) {
            return null;
        }

        EncuestaBienestarResponseDTO dto = new EncuestaBienestarResponseDTO();
        dto.setId(encuesta.getId());
        dto.setEmpleadoId(encuesta.getEmpleado() != null ? encuesta.getEmpleado().getId() : null);
        dto.setFecha(encuesta.getFecha());
        dto.setCargaLaboral(encuesta.getCargaLaboral());
        dto.setApoyoEquipo(encuesta.getApoyoEquipo());
        dto.setProyeccion(encuesta.getProyeccion());
        return dto;
    }
}