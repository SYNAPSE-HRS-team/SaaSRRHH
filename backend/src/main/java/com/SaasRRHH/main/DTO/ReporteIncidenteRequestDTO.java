package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReporteIncidenteRequestDTO {

    private Long empleadoId;

    private Long supervisorId;

    private Long tareaId;

    private Long areaId;

    private String tipo;

    private String descripcion;

    private String evidenciaUrl;

    private String nivelRiesgo;

    private String estado;

    private LocalDateTime fechaIncidente;
}