package com.SaasRRHH.main.DTO; 
import jakarta.persistence.*;

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
        name = "planillas",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_planilla_mes_anio", columnNames = {"mes", "anio"})
        }
)
public class Planilla {

    /** CHECK: estado IN ('PROCESADO','CERRADO') */
    public enum EstadoPlanilla {
        PROCESADO, CERRADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El mes es obligatorio")
    @Min(value = 1, message = "El mes debe ser entre 1 y 12")
    @Max(value = 12, message = "El mes debe ser entre 1 y 12")
    @Column(name = "mes", nullable = false)
    private Integer mes;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 2000, message = "El año no parece válido")
    @Column(name = "anio", nullable = false)
    private Integer anio;

    @DecimalMin(value = "0.00", message = "El total pagado no puede ser negativo")
    @Column(name = "total_pagado", precision = 12, scale = 2)
    private BigDecimal totalPagado;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado",
            length = 20,
            columnDefinition = "VARCHAR(20) DEFAULT 'PROCESADO'"
    )
    private EstadoPlanilla estado = EstadoPlanilla.PROCESADO;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;
}