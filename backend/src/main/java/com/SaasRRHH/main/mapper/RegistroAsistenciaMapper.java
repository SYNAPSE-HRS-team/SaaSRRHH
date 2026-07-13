package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.RegistroAsistenciaRequestDTO;
import com.SaasRRHH.main.DTO.RegistroAsistenciaResponseDTO;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.RegistroAsistencia;

public class RegistroAsistenciaMapper {

    public static RegistroAsistencia toEntity(RegistroAsistenciaRequestDTO dto) {
        RegistroAsistencia registro = new RegistroAsistencia();
        
        Empleado empleado = new Empleado();
        empleado.setId(dto.getEmpleadoId());
        registro.setEmpleado(empleado);
        
        registro.setFechaHora(dto.getFechaHora());
        registro.setTipoMarcacion(dto.getTipoMarcacion());
        registro.setMetodo(dto.getMetodo());
        registro.setEstado(dto.getEstado());
        registro.setObservaciones(dto.getObservaciones());
        
        // ✅ NUEVOS CAMPOS
        registro.setMinutosTardanza(dto.getMinutosTardanza());
        registro.setEsFalta(dto.getEsFalta());
        registro.setJustificado(dto.getJustificado());
        registro.setMotivoJustificacion(dto.getMotivoJustificacion());
        
        return registro;
    }

    public static RegistroAsistenciaResponseDTO toDTO(RegistroAsistencia entity) {
        RegistroAsistenciaResponseDTO dto = new RegistroAsistenciaResponseDTO();
        dto.setId(entity.getId());
        dto.setFechaHora(entity.getFechaHora());
        dto.setTipoMarcacion(entity.getTipoMarcacion());
        dto.setMetodo(entity.getMetodo());
        dto.setEstado(entity.getEstado());
        dto.setObservaciones(entity.getObservaciones());
        
        // ✅ NUEVOS CAMPOS
        dto.setMinutosTardanza(entity.getMinutosTardanza());
        dto.setEsFalta(entity.getEsFalta());
        dto.setJustificado(entity.getJustificado());
        dto.setMotivoJustificacion(entity.getMotivoJustificacion());
        
        if (entity.getEmpleado() != null) {
            dto.setEmpleadoId(entity.getEmpleado().getId());
            dto.setNombreEmpleado(entity.getEmpleado().getNombres() + " " + entity.getEmpleado().getApellidos());
            dto.setDniEmpleado(entity.getEmpleado().getDni());
        }
        
        if (entity.getDispositivo() != null) {
            dto.setDispositivoId(entity.getDispositivo().getId());
        }
        
        return dto;
    }
}