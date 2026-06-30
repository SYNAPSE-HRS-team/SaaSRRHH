package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReporteDiarioResponseDTO {

    private Long id;

    private Long tareaId;

    private Long empleadoId;

    private String descripcionTrabajador;

    private String observacionSupervisor;

    private BigDecimal porcentajeAvance;

    private String estado;

    private LocalDateTime fechaReporte;
}