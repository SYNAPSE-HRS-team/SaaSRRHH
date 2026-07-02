package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.ValidacionSeguridadRequestDTO;
import com.SaasRRHH.main.DTO.ValidacionSeguridadResponseDTO;
import com.SaasRRHH.main.model.DispositivoAutorizado;
import com.SaasRRHH.main.model.RegistroAsistencia;
import com.SaasRRHH.main.model.ValidacionSeguridad;

public class ValidacionSeguridadMapper {

    public static ValidacionSeguridad toEntity(ValidacionSeguridadRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        ValidacionSeguridad validacion = new ValidacionSeguridad();

        if (dto.getAsistenciaId() != null) {
            RegistroAsistencia asistencia = new RegistroAsistencia();
            asistencia.setId(dto.getAsistenciaId());
            validacion.setAsistencia(asistencia);
        }

        if (dto.getDispositivoId() != null) {
            DispositivoAutorizado dispositivo = new DispositivoAutorizado();
            dispositivo.setId(dto.getDispositivoId());
            validacion.setDispositivo(dispositivo);
        }

        validacion.setTotpHash(dto.getTotpHash());
        validacion.setTotpValido(dto.getTotpValido() != null ? dto.getTotpValido() : false);
        return validacion;
    }

    public static ValidacionSeguridadResponseDTO toDTO(ValidacionSeguridad validacion) {
        if (validacion == null) {
            return null;
        }

        ValidacionSeguridadResponseDTO dto = new ValidacionSeguridadResponseDTO();
        dto.setId(validacion.getId());
        dto.setAsistenciaId(validacion.getAsistencia() != null ? validacion.getAsistencia().getId() : null);
        dto.setDispositivoId(validacion.getDispositivo() != null ? validacion.getDispositivo().getId() : null);
        dto.setTotpHash(validacion.getTotpHash());
        dto.setTotpValido(validacion.getTotpValido());
        dto.setFechaValidacion(validacion.getFechaValidacion());
        return dto;
    }
}