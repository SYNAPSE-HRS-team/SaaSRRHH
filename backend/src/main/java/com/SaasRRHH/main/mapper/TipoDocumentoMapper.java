package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.TipoDocumentoRequestDTO;
import com.SaasRRHH.main.DTO.TipoDocumentoResponseDTO;
import com.SaasRRHH.main.model.TipoDocumento;

public class TipoDocumentoMapper {

    // ENTITY → DTO
    public static TipoDocumentoResponseDTO toDTO(TipoDocumento t) {

        TipoDocumentoResponseDTO dto = new TipoDocumentoResponseDTO();

        dto.setIdTipo(t.getIdTipo());
        dto.setNombre(t.getNombre());
        dto.setObligatorio(t.getObligatorio());
        dto.setDiasVigencia(t.getDiasVigencia());
        dto.setRequiereRenovacion(t.getRequiereRenovacion());
        dto.setDescripcion(t.getDescripcion());

        return dto;
    }

    // DTO → ENTITY
    public static TipoDocumento toEntity(TipoDocumentoRequestDTO dto) {

        TipoDocumento t = new TipoDocumento();

        t.setNombre(dto.getNombre());
        t.setObligatorio(dto.getObligatorio());
        t.setDiasVigencia(dto.getDiasVigencia());
        t.setRequiereRenovacion(dto.getRequiereRenovacion());
        t.setDescripcion(dto.getDescripcion());

        return t;
    }
    public static void updateEntity(TipoDocumento existente, TipoDocumentoRequestDTO dto) {

        existente.setNombre(dto.getNombre());
        existente.setObligatorio(dto.getObligatorio());
        existente.setDiasVigencia(dto.getDiasVigencia());
        existente.setRequiereRenovacion(dto.getRequiereRenovacion());
        existente.setDescripcion(dto.getDescripcion());
    }
}