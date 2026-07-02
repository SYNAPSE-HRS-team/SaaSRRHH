package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.AreaTrabajoRequestDTO;
import com.SaasRRHH.main.DTO.AreaTrabajoResponseDTO;
import com.SaasRRHH.main.model.AreaTrabajo;

public class AreaTrabajoMapper {

    public static AreaTrabajoResponseDTO toDTO(AreaTrabajo a) {
        if (a == null) return null;
        AreaTrabajoResponseDTO dto = new AreaTrabajoResponseDTO();
        dto.setId(a.getId());
        dto.setNombre(a.getNombre());
        dto.setCultivoTipo(a.getCultivoTipo());
        dto.setActivo(a.getActivo());
        dto.setFechaRegistro(a.getFechaRegistro());
        return dto;
    }

    public static AreaTrabajo toEntity(AreaTrabajoRequestDTO dto) {
        if (dto == null) return null;
        AreaTrabajo a = new AreaTrabajo();
        a.setId(dto.getId());
        a.setNombre(dto.getNombre());
        a.setCultivoTipo(dto.getCultivoTipo());
        a.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        return a;
    }
}
