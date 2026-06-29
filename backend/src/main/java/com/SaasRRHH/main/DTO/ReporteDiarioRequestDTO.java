package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReporteDiarioRequestDTO {

    private Long tareaId;

    private Long empleadoId;

    private String descripcionTrabajador;

    private String observacionSupervisor;

    private BigDecimal porcentajeAvance;

    private String estado;
}