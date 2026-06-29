package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetricaBurnoutRequestDTO {

    private Long empleadoId;

    private String nivelRiesgo;

    private Integer horasExtraAcumuladas;

    private Boolean tendenciaTardanza;
}