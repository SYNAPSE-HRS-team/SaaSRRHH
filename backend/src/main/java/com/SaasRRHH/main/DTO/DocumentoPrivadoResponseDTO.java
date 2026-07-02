package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class DocumentoPrivadoResponseDTO {

    private Long id;

    private Long empleadoId;

    private String empleadoNombre;

    private Long tipoId;

    private String tipoNombre;

    private String archivoUrl;

    private LocalDate fechaVencimiento;

    private LocalDateTime fechaCarga;

    private Boolean activo;
}