package com.SaasRRHH.main.model;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for Planilla - Non-persistent model
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Planilla {

    public enum EstadoPlanilla {
        PROCESADO, CERRADO
    }

    private Long id;

    @NotNull(message = "El mes es obligatorio")
    @Min(value = 1, message = "El mes debe ser entre 1 y 12")
    @Max(value = 12, message = "El mes debe ser entre 1 y 12")
    private Integer mes;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 2000, message = "El año no parece válido")
    private Integer anio;

    @DecimalMin(value = "0.00", message = "El total pagado no puede ser negativo")
    private BigDecimal totalPagado;

    private EstadoPlanilla estado = EstadoPlanilla.PROCESADO;

    private LocalDateTime fechaCierre;
}
