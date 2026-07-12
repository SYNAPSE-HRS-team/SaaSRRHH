package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.EmpleadoRequestDTO;
import com.SaasRRHH.main.DTO.EmpleadoResponseDTO;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.Usuario;

public class EmpleadoMapper {

    // REQUEST DTO → ENTITY
    public static Empleado toEntity(EmpleadoRequestDTO dto, Usuario usuario) {
        Empleado empleado = new Empleado();
        empleado.setUsuario(usuario);
        empleado.setNombres(dto.getNombres());
        empleado.setApellidos(dto.getApellidos());
        empleado.setDni(dto.getDni());
        empleado.setFotoPerfilUrl(dto.getFotoPerfilUrl());
        empleado.setSueldoBase(dto.getSueldoBase());
        empleado.setAsignacionFamiliar(dto.getAsignacionFamiliar());
        empleado.setFechaInicioContrato(dto.getFechaInicioContrato());
        empleado.setFechaFinContrato(dto.getFechaFinContrato());
        empleado.setCargo(dto.getCargo());
        empleado.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        
        // ✅ NUEVOS CAMPOS
        empleado.setHoraEntrada(dto.getHoraEntrada());
        empleado.setHoraSalida(dto.getHoraSalida());
        empleado.setDiasLaborables(dto.getDiasLaborables());
        empleado.setToleranciaMinutos(dto.getToleranciaMinutos());
        empleado.setTipoPago(dto.getTipoPago());
        empleado.setMontoPago(dto.getMontoPago());
        
        return empleado;
    }

    // ENTITY → RESPONSE DTO
    public static EmpleadoResponseDTO toDTO(Empleado empleado) {
        EmpleadoResponseDTO dto = new EmpleadoResponseDTO();
        dto.setId(empleado.getId());
        dto.setNombres(empleado.getNombres());
        dto.setApellidos(empleado.getApellidos());
        dto.setDni(empleado.getDni());
        dto.setFotoPerfilUrl(empleado.getFotoPerfilUrl());
        dto.setSueldoBase(empleado.getSueldoBase());
        dto.setAsignacionFamiliar(empleado.getAsignacionFamiliar());
        dto.setFechaInicioContrato(empleado.getFechaInicioContrato());
        dto.setFechaFinContrato(empleado.getFechaFinContrato());
        dto.setCargo(empleado.getCargo());
        dto.setActivo(empleado.getActivo());
        dto.setFechaRegistro(empleado.getFechaRegistro());
        
        // ✅ NUEVOS CAMPOS
        dto.setHoraEntrada(empleado.getHoraEntrada());
        dto.setHoraSalida(empleado.getHoraSalida());
        dto.setDiasLaborables(empleado.getDiasLaborables());
        dto.setToleranciaMinutos(empleado.getToleranciaMinutos());
        dto.setTipoPago(empleado.getTipoPago());
        dto.setMontoPago(empleado.getMontoPago());
        
        if (empleado.getUsuario() != null) {
            dto.setUsuarioId(empleado.getUsuario().getId());
            dto.setEmail(empleado.getUsuario().getEmail());
        }
        
        return dto;
    }
    
    // ✅ NUEVO: Actualizar entidad desde DTO (para updates)
    public static void updateEntity(Empleado empleado, EmpleadoRequestDTO dto) {
        if (dto.getNombres() != null) empleado.setNombres(dto.getNombres());
        if (dto.getApellidos() != null) empleado.setApellidos(dto.getApellidos());
        if (dto.getDni() != null) empleado.setDni(dto.getDni());
        if (dto.getFotoPerfilUrl() != null) empleado.setFotoPerfilUrl(dto.getFotoPerfilUrl());
        if (dto.getSueldoBase() != null) empleado.setSueldoBase(dto.getSueldoBase());
        if (dto.getAsignacionFamiliar() != null) empleado.setAsignacionFamiliar(dto.getAsignacionFamiliar());
        if (dto.getFechaInicioContrato() != null) empleado.setFechaInicioContrato(dto.getFechaInicioContrato());
        if (dto.getFechaFinContrato() != null) empleado.setFechaFinContrato(dto.getFechaFinContrato());
        if (dto.getCargo() != null) empleado.setCargo(dto.getCargo());
        if (dto.getActivo() != null) empleado.setActivo(dto.getActivo());
        
        // ✅ NUEVOS CAMPOS
        if (dto.getHoraEntrada() != null) empleado.setHoraEntrada(dto.getHoraEntrada());
        if (dto.getHoraSalida() != null) empleado.setHoraSalida(dto.getHoraSalida());
        if (dto.getDiasLaborables() != null) empleado.setDiasLaborables(dto.getDiasLaborables());
        if (dto.getToleranciaMinutos() != null) empleado.setToleranciaMinutos(dto.getToleranciaMinutos());
        if (dto.getTipoPago() != null) empleado.setTipoPago(dto.getTipoPago());
        if (dto.getMontoPago() != null) empleado.setMontoPago(dto.getMontoPago());
    }
}