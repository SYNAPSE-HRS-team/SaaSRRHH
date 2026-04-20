package com.SaasRRHH.main.model;

import com.SaasRRHH.main.entity.AreaTrabajo;
import com.SaasRRHH.main.entity.Empleado;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for TareaAsignada - Non-persistent model for API transfer
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TareaAsignada {

    /** Funcion enum: CULTIVADOR, ROCIADOR, ARADOR, RECOLECTOR, LIMPIADOR */
    public enum Funcion {
        CULTIVADOR, ROCIADOR, ARADOR, RECOLECTOR, LIMPIADOR
    }

    /** Estado enum: PENDIENTE, EN_PROGRESO, COMPLETADO, CANCELADO */
    public enum EstadoTarea {
        PENDIENTE, EN_PROGRESO, COMPLETADO, CANCELADO
    }

    private Long id;

    @NotNull(message = "El empleado es obligatorio")
    private Empleado empleado;

    @NotNull(message = "El supervisor es obligatorio")
    private Empleado supervisor;

    @NotNull(message = "El área es obligatoria")
    private AreaTrabajo area;

    @NotNull(message = "La función es obligatoria")
    private Funcion funcion;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    private String descripcion;

    private EstadoTarea estado = EstadoTarea.PENDIENTE;

    private LocalDateTime fechaRegistro;
}
