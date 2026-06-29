package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.TareaAsignadaRequestDTO;
import com.SaasRRHH.main.DTO.TareaAsignadaResponseDTO;
import com.SaasRRHH.main.model.AreaTrabajo;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.TareaAsignada;

public class TareaAsignadaMapper {

    public static TareaAsignadaResponseDTO toDTO(TareaAsignada t) {
        if (t == null) return null;
        TareaAsignadaResponseDTO dto = new TareaAsignadaResponseDTO();
        dto.setId(t.getId());
        dto.setEmpleadoId(t.getEmpleado() != null ? t.getEmpleado().getId() : null);
        dto.setSupervisorId(t.getSupervisor() != null ? t.getSupervisor().getId() : null);
        dto.setAreaId(t.getArea() != null ? t.getArea().getId() : null);
        dto.setFuncion(t.getFuncion() != null ? t.getFuncion().name() : null);
        dto.setFecha(t.getFecha());
        dto.setDescripcion(t.getDescripcion());
        dto.setEstado(t.getEstado() != null ? t.getEstado().name() : null);
        dto.setFechaRegistro(t.getFechaRegistro());
        return dto;
    }

    public static TareaAsignada toEntity(TareaAsignadaRequestDTO dto) {
        if (dto == null) return null;
        TareaAsignada t = new TareaAsignada();
        t.setId(dto.getId());
        if (dto.getEmpleadoId() != null) {
            Empleado e = new Empleado();
            e.setId(dto.getEmpleadoId());
            t.setEmpleado(e);
        }
        if (dto.getSupervisorId() != null) {
            Empleado s = new Empleado();
            s.setId(dto.getSupervisorId());
            t.setSupervisor(s);
        }
        if (dto.getAreaId() != null) {
            AreaTrabajo a = new AreaTrabajo();
            a.setId(dto.getAreaId());
            t.setArea(a);
        }
        if (dto.getFuncion() != null) {
            try {
                t.setFuncion(TareaAsignada.Funcion.valueOf(dto.getFuncion()));
            } catch (IllegalArgumentException ignored) {
            }
        }
        t.setFecha(dto.getFecha());
        t.setDescripcion(dto.getDescripcion());
        if (dto.getEstado() != null) {
            try {
                t.setEstado(TareaAsignada.EstadoTarea.valueOf(dto.getEstado()));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return t;
    }
}
