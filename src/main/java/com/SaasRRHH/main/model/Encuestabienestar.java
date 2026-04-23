package com.SaasRRHH.main.model;

import com.SaasRRHH.main.model.Empleado;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for Encuestabienestar - Non-persistent model
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Encuestabienestar {

    private Long id;

    @NotNull(message = "El empleado es obligatorio")
    private Empleado empleado;

    @NotNull(message = "La fecha de la encuesta es obligatoria")
    private LocalDate fecha;

    @Min(value = 1, message = "Debe ser entre 1 y 5")
    @Max(value = 5, message = "Debe ser entre 1 y 5")
    private Integer cargaLaboral;

    @Min(value = 1, message = "Debe ser entre 1 y 5")
    @Max(value = 5, message = "Debe ser entre 1 y 5")
    private Integer apoyoEquipo;

    @Min(value = 1, message = "Debe ser entre 1 y 5")
    @Max(value = 5, message = "Debe ser entre 1 y 5")
    private Integer proyeccion;
}
