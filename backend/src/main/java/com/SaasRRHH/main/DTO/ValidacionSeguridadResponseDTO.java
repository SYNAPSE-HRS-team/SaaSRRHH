package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ValidacionSeguridadResponseDTO {

    private Long id;

    private Long asistenciaId;

    private Long dispositivoId;

    private String totpHash;

    private Boolean totpValido;

    private LocalDateTime fechaValidacion;
}