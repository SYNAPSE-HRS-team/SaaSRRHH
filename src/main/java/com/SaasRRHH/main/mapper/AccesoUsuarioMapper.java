package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.AccesoUsuarioRequestDTO;
import com.SaasRRHH.main.DTO.AccesoUsuarioResponseDTO;
import com.SaasRRHH.main.model.AccesoUsuario;
import com.SaasRRHH.main.model.Usuario;

public class AccesoUsuarioMapper {

    public static AccesoUsuarioResponseDTO toDTO(AccesoUsuario a) {
        if (a == null) return null;
        AccesoUsuarioResponseDTO dto = new AccesoUsuarioResponseDTO();
        dto.setIdAcceso(a.getIdAcceso());
        dto.setUsuarioId(a.getUsuario() != null ? a.getUsuario().getId() : null);
        dto.setFechaLogin(a.getFechaLogin());
        dto.setFechaLogout(a.getFechaLogout());
        dto.setUserAgent(a.getUserAgent());
        dto.setExitoso(a.getExitoso());
        return dto;
    }

    public static AccesoUsuario toEntity(AccesoUsuarioRequestDTO dto) {
        if (dto == null) return null;
        AccesoUsuario a = new AccesoUsuario();
        a.setIdAcceso(dto.getIdAcceso());
        if (dto.getUsuarioId() != null) {
            Usuario u = new Usuario();
            u.setId(dto.getUsuarioId());
            a.setUsuario(u);
        }
        a.setFechaLogin(dto.getFechaLogin());
        a.setFechaLogout(dto.getFechaLogout());
        a.setUserAgent(dto.getUserAgent());
        a.setExitoso(dto.getExitoso() != null ? dto.getExitoso() : true);
        return a;
    }
}
