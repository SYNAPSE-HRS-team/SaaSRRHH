package com.SaasRRHH.main.DTO; 

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

import com.SaasRRHH.main.model.AreaTrabajo;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.TareaAsignada;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for ReporteIncidente - Non-persistent model
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteIncidente {

    public enum TipoIncidente {
        ACTO_SEGURO, ACTO_INSEGURO, INCIDENTE, ACCIDENTE
    }

    public enum NivelRiesgo {
        BAJO, MEDIO, ALTO, CRITICO
    }

    public enum EstadoIncidente {
        REPORTADO, EN_REVISION, CERRADO
    }

    private Long id;

    @NotNull(message = "El empleado es obligatorio")
    private Empleado empleado;

    private Empleado supervisor;

    private TareaAsignada tarea;

    private AreaTrabajo area;

    @NotNull(message = "El tipo de incidente es obligatorio")
    private TipoIncidente tipo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 1000, message = "La descripción no puede superar 1000 caracteres")
    private String descripcion;

    @Size(max = 500, message = "La evidencia no puede superar 500 caracteres")
    private String evidenciaUrl;

    private NivelRiesgo nivelRiesgo;

    private EstadoIncidente estado = EstadoIncidente.REPORTADO;

    @NotNull(message = "La fecha del incidente es obligatoria")
    private LocalDateTime fechaIncidente;

    private LocalDateTime fechaRegistro;
}
