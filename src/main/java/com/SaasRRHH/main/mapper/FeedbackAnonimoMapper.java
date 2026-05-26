package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.FeedbackAnonimoRequestDTO;
import com.SaasRRHH.main.DTO.FeedbackAnonimoResponseDTO;
import com.SaasRRHH.main.model.FeedbackAnonimo;

public class FeedbackAnonimoMapper {

    public static FeedbackAnonimo toEntity(FeedbackAnonimoRequestDTO dto) {
        if (dto == null)
            return null;
        FeedbackAnonimo f = new FeedbackAnonimo();
        f.setMensaje(dto.getMensaje());
        f.setCategoria(dto.getCategoria());
        // estado y fecha se manejan en la entidad
        return f;
    }

    public static FeedbackAnonimoResponseDTO toDTO(FeedbackAnonimo entidad) {
        if (entidad == null)
            return null;
        FeedbackAnonimoResponseDTO dto = new FeedbackAnonimoResponseDTO();
        dto.setId(entidad.getId());
        dto.setMensaje(entidad.getMensaje());
        dto.setCategoria(entidad.getCategoria());
        dto.setEstado(entidad.getEstado());
        dto.setFechaEnvio(entidad.getFechaEnvio());
        return dto;
    }
}
