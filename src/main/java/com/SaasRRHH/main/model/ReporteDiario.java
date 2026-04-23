package com.SaasRRHH.main.model;

import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.TareaAsignada;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for ReporteDiario - Non-persistent model
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDiario {

    public enum EstadoReporte {
        PENDIENTE, VALIDADO, OBSERVADO
    }

    private Long id;

    @NotNull(message = "La tarea es obligatoria")
    private TareaAsignada tarea;

    @NotNull(message = "El empleado es obligatorio")
    private Empleado empleado;

    @Size(max = 1000, message = "La descripción del trabajador no puede superar 1000 caracteres")
    private String descripcionTrabajador;

    @Size(max = 1000, message = "La observación del supervisor no puede superar 1000 caracteres")
    private String observacionSupervisor;

    @DecimalMin(value = "0.00", message = "El porcentaje no puede ser negativo")
    @DecimalMax(value = "100.00", message = "El porcentaje no puede superar 100")
    private BigDecimal porcentajeAvance = BigDecimal.ZERO;

    private EstadoReporte estado = EstadoReporte.PENDIENTE;

    private LocalDateTime fechaReporte;
}
