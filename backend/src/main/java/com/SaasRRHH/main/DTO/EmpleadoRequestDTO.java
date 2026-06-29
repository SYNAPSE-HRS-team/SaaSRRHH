package com.SaasRRHH.main.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpleadoRequestDTO {

    private Long usuarioId;

    private String nombres;

    private String apellidos;

    private String dni;

    private String fotoPerfilUrl;

    private BigDecimal sueldoBase;

    private Boolean asignacionFamiliar;

    private LocalDate fechaInicioContrato;

    private LocalDate fechaFinContrato;

    private String cargo;

    private Boolean activo;
}