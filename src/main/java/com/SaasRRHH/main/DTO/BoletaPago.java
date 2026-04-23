package com.SaasRRHH.main.DTO;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.SaasRRHH.main.model.Empleado;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for BoletaPago - Non-persistent model
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoletaPago {

    private Long id;

    @NotNull(message = "El empleado es obligatorio")
    private Empleado empleado;

    @NotNull(message = "La planilla es obligatoria")
    private com.SaasRRHH.main.DTO.Planilla planilla;  // Reference model DTO

    @NotNull(message = "El sueldo base es obligatorio")
    @DecimalMin(value = "0.00", message = "El sueldo base no puede ser negativo")
    private BigDecimal sueldoBase;

    @Min(value = 0, message = "Los días trabajados no pueden ser negativos")
    private Integer diasTrabajados = 0;

    @Min(value = 0, message = "Los días no trabajados no pueden ser negativos")
    private Integer diasNoTrabajados = 0;

    @DecimalMin(value = "0.00")
    private BigDecimal asignacionFamiliar = BigDecimal.ZERO;

    @DecimalMin(value = "0.00")
    private BigDecimal bonoBeta = BigDecimal.ZERO;

    @DecimalMin(value = "0.00")
    private BigDecimal horasExtraPago = BigDecimal.ZERO;

    @DecimalMin(value = "0.00")
    private BigDecimal otrosBonos = BigDecimal.ZERO;

    @DecimalMin(value = "0.00")
    private BigDecimal descuentoInasistencia = BigDecimal.ZERO;

    @DecimalMin(value = "0.00")
    private BigDecimal otrosDescuentos = BigDecimal.ZERO;

    @DecimalMin(value = "0.00")
    private BigDecimal totalIngresos = BigDecimal.ZERO;

    @DecimalMin(value = "0.00")
    private BigDecimal totalDescuentos = BigDecimal.ZERO;

    @NotNull(message = "El neto a pagar es obligatorio")
    private BigDecimal netoPagar;

    private LocalDateTime fechaEmision;
}
