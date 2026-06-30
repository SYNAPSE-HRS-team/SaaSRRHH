package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.RolResponseDTO;
import com.SaasRRHH.main.model.Rol;

public class RolMapper {

    public static RolResponseDTO toDTO(Rol r) {
        if (r == null) return null;
        RolResponseDTO dto = new RolResponseDTO();
        dto.setIdRol(r.getIdRol());
        dto.setNombreRol(r.getNombreRol());
        return dto;
    }
}
