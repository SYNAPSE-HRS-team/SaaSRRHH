package com.SaasRRHH.main.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpleadoResponseDTO {

    private Long id;

    private Long usuarioId;

    private String email;

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

    private LocalDateTime fechaRegistro;
}