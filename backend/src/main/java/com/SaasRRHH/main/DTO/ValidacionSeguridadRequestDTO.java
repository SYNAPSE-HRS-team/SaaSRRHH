package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidacionSeguridadRequestDTO {

    private Long asistenciaId;

    private Long dispositivoId;

    private String totpHash;

    private Boolean totpValido;
}