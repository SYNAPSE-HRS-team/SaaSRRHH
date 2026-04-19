package com.SaasRRHH.main.model;

import com.SaasRRHH.main.entity.Empleado;
import jakarta.persistence.*;

import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "boletas_pago",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_boleta", columnNames = {"empleado_id", "planilla_id"})
        },
        indexes = {
                @Index(name = "idx_empleado", columnList = "empleado_id"),
                @Index(name = "idx_planilla", columnList = "planilla_id")
        }
)
public class BoletaPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El empleado es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "empleado_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_boleta_empleado")
    )
    private Empleado empleado;

    @NotNull(message = "La planilla es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "planilla_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_boleta_planilla")
    )
    private Planilla planilla;


    @NotNull(message = "El sueldo base es obligatorio")
    @DecimalMin(value = "0.00", message = "El sueldo base no puede ser negativo")
    @Column(name = "sueldo_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal sueldoBase;

    @Min(value = 0, message = "Los días trabajados no pueden ser negativos")
    @Column(name = "dias_trabajados", nullable = false,
            columnDefinition = "INT DEFAULT 0")
    private Integer diasTrabajados = 0;

    @Min(value = 0, message = "Los días no trabajados no pueden ser negativos")
    @Column(name = "dias_no_trabajados", nullable = false,
            columnDefinition = "INT DEFAULT 0")
    private Integer diasNoTrabajados = 0;

    @DecimalMin(value = "0.00")
    @Column(name = "asignacion_familiar", precision = 10, scale = 2,
            columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private BigDecimal asignacionFamiliar = BigDecimal.ZERO;

    @DecimalMin(value = "0.00")
    @Column(name = "bono_beta", precision = 10, scale = 2,
            columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private BigDecimal bonoBeta = BigDecimal.ZERO;

    @DecimalMin(value = "0.00")
    @Column(name = "horas_extra_pago", precision = 10, scale = 2,
            columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private BigDecimal horasExtraPago = BigDecimal.ZERO;

    @DecimalMin(value = "0.00")
    @Column(name = "otros_bonos", precision = 10, scale = 2,
            columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private BigDecimal otrosBonos = BigDecimal.ZERO;


    @DecimalMin(value = "0.00")
    @Column(name = "descuento_inasistencia", precision = 10, scale = 2,
            columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private BigDecimal descuentoInasistencia = BigDecimal.ZERO;

    @DecimalMin(value = "0.00")
    @Column(name = "otros_descuentos", precision = 10, scale = 2,
            columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private BigDecimal otrosDescuentos = BigDecimal.ZERO;


    @DecimalMin(value = "0.00")
    @Column(name = "total_ingresos", precision = 12, scale = 2,
            columnDefinition = "DECIMAL(12,2) DEFAULT 0.00")
    private BigDecimal totalIngresos = BigDecimal.ZERO;

    @DecimalMin(value = "0.00")
    @Column(name = "total_descuentos", precision = 12, scale = 2,
            columnDefinition = "DECIMAL(12,2) DEFAULT 0.00")
    private BigDecimal totalDescuentos = BigDecimal.ZERO;

    @NotNull(message = "El neto a pagar es obligatorio")
    @Column(name = "neto_pagar", nullable = false, precision = 12, scale = 2)
    private BigDecimal netoPagar;

    @Column(
            name = "fecha_emision",
            nullable = false,
            insertable = false,
            updatable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP"
    )
    private LocalDateTime fechaEmision;
}