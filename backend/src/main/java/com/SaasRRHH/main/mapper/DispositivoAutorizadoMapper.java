package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.DispositivoAutorizadoRequestDTO;
import com.SaasRRHH.main.DTO.DispositivoAutorizadoResponseDTO;
import com.SaasRRHH.main.model.DispositivoAutorizado;
import com.SaasRRHH.main.model.Usuario;

public class DispositivoAutorizadoMapper {

    public static DispositivoAutorizadoResponseDTO toDTO(DispositivoAutorizado d) {
        if (d == null) return null;
        DispositivoAutorizadoResponseDTO dto = new DispositivoAutorizadoResponseDTO();
        dto.setId(d.getId());
        dto.setUsuarioId(d.getUsuario() != null ? d.getUsuario().getId() : null);
        dto.setHardwareId(d.getHardwareId());
        dto.setFcmToken(d.getFcmToken());
        dto.setActivo(d.getActivo());
        dto.setFechaRegistro(d.getFechaRegistro());
        return dto;
    }

    public static DispositivoAutorizado toEntity(DispositivoAutorizadoRequestDTO dto) {
        if (dto == null) return null;
        DispositivoAutorizado d = new DispositivoAutorizado();
        d.setId(dto.getId());
        if (dto.getUsuarioId() != null) {
            Usuario u = new Usuario();
            u.setId(dto.getUsuarioId());
            d.setUsuario(u);
        }
        d.setHardwareId(dto.getHardwareId());
        d.setFcmToken(dto.getFcmToken());
        d.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        return d;
    }
}
