package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EncuestaBienestarRequestDTO {

    private Long empleadoId;

    private LocalDate fecha;

    private Integer cargaLaboral;

    private Integer apoyoEquipo;

    private Integer proyeccion;
}