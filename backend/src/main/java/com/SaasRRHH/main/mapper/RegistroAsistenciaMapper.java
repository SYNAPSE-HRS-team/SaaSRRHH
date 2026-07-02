package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.RegistroAsistenciaRequestDTO;
import com.SaasRRHH.main.DTO.RegistroAsistenciaResponseDTO;
import com.SaasRRHH.main.model.DispositivoAutorizado;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.RegistroAsistencia;

public class RegistroAsistenciaMapper {

    public static RegistroAsistenciaResponseDTO toDTO(RegistroAsistencia r) {
        if (r == null) return null;
        RegistroAsistenciaResponseDTO dto = new RegistroAsistenciaResponseDTO();
        dto.setId(r.getId());
        dto.setEmpleadoId(r.getEmpleado() != null ? r.getEmpleado().getId() : null);
        dto.setDispositivoId(r.getDispositivo() != null ? r.getDispositivo().getId() : null);
        dto.setFechaHora(r.getFechaHora());
        dto.setTipoMarcacion(r.getTipoMarcacion());
        dto.setMetodo(r.getMetodo());
        dto.setEstado(r.getEstado());
        dto.setObservaciones(r.getObservaciones());
        return dto;
    }

    public static RegistroAsistencia toEntity(RegistroAsistenciaRequestDTO dto) {
        if (dto == null) return null;
        RegistroAsistencia r = new RegistroAsistencia();
        r.setId(dto.getId());
        if (dto.getEmpleadoId() != null) {
            Empleado e = new Empleado();
            e.setId(dto.getEmpleadoId());
            r.setEmpleado(e);
        }
        if (dto.getDispositivoId() != null) {
            DispositivoAutorizado d = new DispositivoAutorizado();
            d.setId(dto.getDispositivoId());
            r.setDispositivo(d);
        }
        r.setFechaHora(dto.getFechaHora());
        r.setTipoMarcacion(dto.getTipoMarcacion());
        r.setMetodo(dto.getMetodo());
        r.setEstado(dto.getEstado());
        r.setObservaciones(dto.getObservaciones());
        return r;
    }
}
