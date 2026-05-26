package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PlanillaResponseDTO {

    private Long id;

    private Integer mes;

    private Integer anio;

    private BigDecimal totalPagado;

    private String estado;

    private LocalDateTime fechaCierre;
}