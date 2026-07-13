package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.FeedbackAnonimoRequestDTO;
import com.SaasRRHH.main.DTO.FeedbackAnonimoResponseDTO;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.FeedbackAnonimo;

public class FeedbackAnonimoMapper {

    public static FeedbackAnonimo toEntity(FeedbackAnonimoRequestDTO dto) {
        FeedbackAnonimo feedback = new FeedbackAnonimo();
        feedback.setMensaje(dto.getMensaje());
        
        if (dto.getCategoria() != null) {
            feedback.setCategoria(FeedbackAnonimo.CategoriaFeedback.valueOf(dto.getCategoria()));
        }
        
        // ✅ NUEVOS CAMPOS
        feedback.setEsAnonimo(dto.getEsAnonimo() != null ? dto.getEsAnonimo() : true);
        feedback.setRespuesta(dto.getRespuesta());
        
        return feedback;
    }

    public static FeedbackAnonimoResponseDTO toDTO(FeedbackAnonimo entity) {
        FeedbackAnonimoResponseDTO dto = new FeedbackAnonimoResponseDTO();
        dto.setId(entity.getId());
        dto.setMensaje(entity.getMensaje());
        dto.setCategoria(entity.getCategoria().name());
        dto.setEstado(entity.getEstado().name());
        dto.setFechaEnvio(entity.getFechaEnvio());
        
        // ✅ NUEVOS CAMPOS
        dto.setEsAnonimo(entity.getEsAnonimo());
        dto.setRespuesta(entity.getRespuesta());
        dto.setFechaRespuesta(entity.getFechaRespuesta());
        
        if (entity.getEmpleado() != null) {
            dto.setEmpleadoId(entity.getEmpleado().getId());
            // Solo mostrar nombre si NO es anónimo
            if (entity.getEsAnonimo() == null || !entity.getEsAnonimo()) {
                dto.setNombreEmpleado(entity.getEmpleado().getNombres() + " " + entity.getEmpleado().getApellidos());
            }
        }
        
        return dto;
    }
}