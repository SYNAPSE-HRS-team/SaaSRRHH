package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.FamiliarRequestDTO;
import com.SaasRRHH.main.DTO.FamiliarResponseDTO;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.Familiar;

import org.springframework.stereotype.Component;

@Component
public class FamiliarMapper {

    // ===================================
    // ENTITY -> RESPONSE DTO
    // ===================================

    public FamiliarResponseDTO toDTO(Familiar f) {

        FamiliarResponseDTO dto =
                new FamiliarResponseDTO();

        dto.setId(f.getId());

        dto.setEmpleadoId(
                f.getEmpleado().getId());

        dto.setEmpleadoNombre(
                f.getEmpleado().getNombres()
                        + " "
                        + f.getEmpleado().getApellidos());

        dto.setParentesco(
                f.getParentesco());

        dto.setNombres(
                f.getNombres());

        dto.setDniFamiliar(
                f.getDniFamiliar());

        dto.setFechaNacimiento(
                f.getFechaNacimiento());

        dto.setEstudia(
                f.getEstudia());

        dto.setActivo(
                f.getActivo());

        return dto;
    }

    // ===================================
    // REQUEST DTO -> ENTITY
    // ===================================

    public Familiar toEntity(
            FamiliarRequestDTO dto,
            Empleado empleado) {

        Familiar f = new Familiar();

        f.setEmpleado(empleado);

        f.setParentesco(
                dto.getParentesco());

        f.setNombres(
                dto.getNombres());

        f.setDniFamiliar(
                dto.getDniFamiliar());

        f.setFechaNacimiento(
                dto.getFechaNacimiento());

        f.setEstudia(
                dto.getEstudia());

        f.setActivo(
                dto.getActivo());

        return f;
    }

    // ===================================
    // UPDATE ENTITY
    // ===================================

    public void updateEntity(
            Familiar f,
            FamiliarRequestDTO dto,
            Empleado empleado) {

        f.setEmpleado(empleado);

        f.setParentesco(
                dto.getParentesco());

        f.setNombres(
                dto.getNombres());

        f.setDniFamiliar(
                dto.getDniFamiliar());

        f.setFechaNacimiento(
                dto.getFechaNacimiento());

        f.setEstudia(
                dto.getEstudia());

        f.setActivo(
                dto.getActivo());
    }
}