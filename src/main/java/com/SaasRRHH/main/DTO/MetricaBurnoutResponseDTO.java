package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MetricaBurnoutResponseDTO {

    private Long id;

    private Long empleadoId;

    private String nivelRiesgo;

    private Integer horasExtraAcumuladas;

    private Boolean tendenciaTardanza;

    private LocalDateTime fechaEvaluacion;
}