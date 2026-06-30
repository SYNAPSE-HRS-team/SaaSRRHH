package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.EmpleadoRequestDTO;
import com.SaasRRHH.main.DTO.EmpleadoResponseDTO;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.Usuario;

public class EmpleadoMapper {

    public static EmpleadoResponseDTO toDTO(Empleado e) {

        EmpleadoResponseDTO dto = new EmpleadoResponseDTO();

        dto.setId(e.getId());

        if (e.getUsuario() != null) {
            dto.setUsuarioId(e.getUsuario().getId());
            dto.setEmail(e.getUsuario().getEmail());
        }

        dto.setNombres(e.getNombres());
        dto.setApellidos(e.getApellidos());
        dto.setDni(e.getDni());
        dto.setFotoPerfilUrl(e.getFotoPerfilUrl());
        dto.setSueldoBase(e.getSueldoBase());
        dto.setAsignacionFamiliar(e.getAsignacionFamiliar());
        dto.setFechaInicioContrato(e.getFechaInicioContrato());
        dto.setFechaFinContrato(e.getFechaFinContrato());
        dto.setCargo(e.getCargo());
        dto.setActivo(e.getActivo());
        dto.setFechaRegistro(e.getFechaRegistro());

        return dto;
    }

    public static Empleado toEntity(EmpleadoRequestDTO dto, Usuario usuario) {

        Empleado e = new Empleado();

        e.setUsuario(usuario);
        e.setNombres(dto.getNombres());
        e.setApellidos(dto.getApellidos());
        e.setDni(dto.getDni());
        e.setFotoPerfilUrl(dto.getFotoPerfilUrl());
        e.setSueldoBase(dto.getSueldoBase());
        e.setAsignacionFamiliar(dto.getAsignacionFamiliar());
        e.setFechaInicioContrato(dto.getFechaInicioContrato());
        e.setFechaFinContrato(dto.getFechaFinContrato());
        e.setCargo(dto.getCargo());
        e.setActivo(dto.getActivo());

        return e;
    }
}