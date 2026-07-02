package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.ReporteIncidenteRequestDTO;
import com.SaasRRHH.main.DTO.ReporteIncidenteResponseDTO;
import com.SaasRRHH.main.model.AreaTrabajo;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.ReporteIncidente;
import com.SaasRRHH.main.model.TareaAsignada;

public class ReporteIncidenteMapper {

    public static ReporteIncidente toEntity(ReporteIncidenteRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        ReporteIncidente reporte = new ReporteIncidente();

        if (dto.getEmpleadoId() != null) {
            Empleado empleado = new Empleado();
            empleado.setId(dto.getEmpleadoId());
            reporte.setEmpleado(empleado);
        }
        if (dto.getSupervisorId() != null) {
            Empleado supervisor = new Empleado();
            supervisor.setId(dto.getSupervisorId());
            reporte.setSupervisor(supervisor);
        }
        if (dto.getTareaId() != null) {
            TareaAsignada tarea = new TareaAsignada();
            tarea.setId(dto.getTareaId());
            reporte.setTarea(tarea);
        }
        if (dto.getAreaId() != null) {
            AreaTrabajo area = new AreaTrabajo();
            area.setId(dto.getAreaId());
            reporte.setArea(area);
        }

        if (dto.getTipo() != null) {
            reporte.setTipo(ReporteIncidente.TipoIncidente.valueOf(dto.getTipo()));
        }

        reporte.setDescripcion(dto.getDescripcion());
        reporte.setEvidenciaUrl(dto.getEvidenciaUrl());

        if (dto.getNivelRiesgo() != null) {
            reporte.setNivelRiesgo(ReporteIncidente.NivelRiesgo.valueOf(dto.getNivelRiesgo()));
        }

        if (dto.getEstado() != null) {
            reporte.setEstado(ReporteIncidente.EstadoIncidente.valueOf(dto.getEstado()));
        }

        reporte.setFechaIncidente(dto.getFechaIncidente());
        return reporte;
    }

    public static ReporteIncidenteResponseDTO toDTO(ReporteIncidente reporte) {
        if (reporte == null) {
            return null;
        }

        ReporteIncidenteResponseDTO dto = new ReporteIncidenteResponseDTO();
        dto.setId(reporte.getId());

        // ✅ IDs (por compatibilidad)
        dto.setEmpleadoId(reporte.getEmpleado() != null ? reporte.getEmpleado().getId() : null);
        dto.setSupervisorId(reporte.getSupervisor() != null ? reporte.getSupervisor().getId() : null);
        dto.setTareaId(reporte.getTarea() != null ? reporte.getTarea().getId() : null);
        dto.setAreaId(reporte.getArea() != null ? reporte.getArea().getId() : null);

        // ✅ MAPEAR OBJETOS COMPLETOS
        if (reporte.getEmpleado() != null) {
            dto.setEmpleado(EmpleadoMapper.toDTO(reporte.getEmpleado()));
        }
        if (reporte.getSupervisor() != null) {
            dto.setSupervisor(EmpleadoMapper.toDTO(reporte.getSupervisor()));
        }
        if (reporte.getTarea() != null) {
            dto.setTarea(TareaAsignadaMapper.toDTO(reporte.getTarea()));
        }
        if (reporte.getArea() != null) {
            dto.setArea(AreaTrabajoMapper.toDTO(reporte.getArea()));
        }

        dto.setTipo(reporte.getTipo() != null ? reporte.getTipo().name() : null);
        dto.setDescripcion(reporte.getDescripcion());
        dto.setEvidenciaUrl(reporte.getEvidenciaUrl());
        dto.setNivelRiesgo(reporte.getNivelRiesgo() != null ? reporte.getNivelRiesgo().name() : null);
        dto.setEstado(reporte.getEstado() != null ? reporte.getEstado().name() : null);
        dto.setFechaIncidente(reporte.getFechaIncidente());
        dto.setFechaRegistro(reporte.getFechaRegistro());

        return dto;
    }
}