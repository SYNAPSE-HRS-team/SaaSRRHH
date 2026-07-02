package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.ReporteDiarioRequestDTO;
import com.SaasRRHH.main.DTO.ReporteDiarioResponseDTO;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.ReporteDiario;
import com.SaasRRHH.main.model.TareaAsignada;

public class ReporteDiarioMapper {

    public static ReporteDiario toEntity(ReporteDiarioRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        ReporteDiario reporte = new ReporteDiario();
        if (dto.getTareaId() != null) {
            TareaAsignada tarea = new TareaAsignada();
            tarea.setId(dto.getTareaId());
            reporte.setTarea(tarea);
        }
        if (dto.getEmpleadoId() != null) {
            Empleado empleado = new Empleado();
            empleado.setId(dto.getEmpleadoId());
            reporte.setEmpleado(empleado);
        }
        reporte.setDescripcionTrabajador(dto.getDescripcionTrabajador());
        reporte.setObservacionSupervisor(dto.getObservacionSupervisor());
        reporte.setPorcentajeAvance(dto.getPorcentajeAvance());
        if (dto.getEstado() != null) {
            reporte.setEstado(ReporteDiario.EstadoReporte.valueOf(dto.getEstado()));
        }
        return reporte;
    }

    public static ReporteDiarioResponseDTO toDTO(ReporteDiario reporte) {
        if (reporte == null) {
            return null;
        }

        ReporteDiarioResponseDTO dto = new ReporteDiarioResponseDTO();
        dto.setId(reporte.getId());
        dto.setTareaId(reporte.getTarea() != null ? reporte.getTarea().getId() : null);
        dto.setEmpleadoId(reporte.getEmpleado() != null ? reporte.getEmpleado().getId() : null);
        dto.setDescripcionTrabajador(reporte.getDescripcionTrabajador());
        dto.setObservacionSupervisor(reporte.getObservacionSupervisor());
        dto.setPorcentajeAvance(reporte.getPorcentajeAvance());
        dto.setEstado(reporte.getEstado() != null ? reporte.getEstado().name() : null);
        dto.setFechaReporte(reporte.getFechaReporte());
        return dto;
    }
}