package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DocumentoPrivadoRequestDTO {

    private Long empleadoId;

    private Long tipoId;

    private String archivoUrl;

    private LocalDate fechaVencimiento;

    private Boolean activo;
}