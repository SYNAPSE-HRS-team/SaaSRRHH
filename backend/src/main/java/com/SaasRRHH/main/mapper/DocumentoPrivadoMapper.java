package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.DocumentoPrivadoRequestDTO;
import com.SaasRRHH.main.DTO.DocumentoPrivadoResponseDTO;
import com.SaasRRHH.main.model.DocumentoPrivado;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.TipoDocumento;

public class DocumentoPrivadoMapper {

    // REQUEST DTO → ENTITY
    public static DocumentoPrivado toEntity(
            DocumentoPrivadoRequestDTO dto,
            Empleado empleado,
            TipoDocumento tipo
    ) {

        DocumentoPrivado doc = new DocumentoPrivado();

        doc.setEmpleado(empleado);
        doc.setTipo(tipo);
        doc.setArchivoUrl(dto.getArchivoUrl());
        doc.setFechaVencimiento(dto.getFechaVencimiento());
        doc.setFecha_emision(dto.getFechaEmision());
        doc.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

        return doc;
    }

    // ENTITY → RESPONSE DTO
    public static DocumentoPrivadoResponseDTO toDTO(DocumentoPrivado doc) {

        DocumentoPrivadoResponseDTO dto = new DocumentoPrivadoResponseDTO();

        dto.setId(doc.getId());
        dto.setArchivoUrl(doc.getArchivoUrl());
        dto.setFechaVencimiento(doc.getFechaVencimiento());
        dto.setFechaEmesion(doc.getFecha_emision());
        dto.setFechaCarga(doc.getFechaCarga());

        dto.setActivo(doc.getActivo());

        if (doc.getEmpleado() != null) {
            dto.setEmpleadoId(doc.getEmpleado().getId());
            dto.setEmpleadoNombre(doc.getEmpleado().getNombres());
        }

        if (doc.getTipo() != null) {
            dto.setTipoId(doc.getTipo().getIdTipo());
            dto.setTipoNombre(doc.getTipo().getNombre());
        }

        return dto;
    }
}