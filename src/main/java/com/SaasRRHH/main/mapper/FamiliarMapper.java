package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.FamiliarDTO;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.Familiar;
import org.springframework.stereotype.Component;

@Component
public class FamiliarMapper {

    // ENTITY → DTO
    public FamiliarDTO toDTO(Familiar f) {

        FamiliarDTO dto = new FamiliarDTO();

        dto.setId(f.getId());
        dto.setEmpleadoId(f.getEmpleado().getId());
        dto.setParentesco(f.getParentesco());
        dto.setNombres(f.getNombres());
        dto.setDniFamiliar(f.getDniFamiliar());
        dto.setFechaNacimiento(f.getFechaNacimiento());
        dto.setEstudia(f.getEstudia());
        dto.setActivo(f.getActivo());

        return dto;
    }

    // DTO → ENTITY (CREATE)
    public Familiar toEntity(FamiliarDTO dto, Empleado empleado) {

        Familiar f = new Familiar();

        f.setEmpleado(empleado);
        f.setParentesco(dto.getParentesco());
        f.setNombres(dto.getNombres());
        f.setDniFamiliar(dto.getDniFamiliar());
        f.setFechaNacimiento(dto.getFechaNacimiento());
        f.setEstudia(dto.getEstudia());
        f.setActivo(dto.getActivo());

        return f;
    }

    // UPDATE ENTITY (sin crear nuevo objeto)
    public void updateEntity(Familiar f, FamiliarDTO dto, Empleado empleado) {

        f.setEmpleado(empleado);
        f.setParentesco(dto.getParentesco());
        f.setNombres(dto.getNombres());
        f.setDniFamiliar(dto.getDniFamiliar());
        f.setFechaNacimiento(dto.getFechaNacimiento());
        f.setEstudia(dto.getEstudia());
        f.setActivo(dto.getActivo());
    }
}