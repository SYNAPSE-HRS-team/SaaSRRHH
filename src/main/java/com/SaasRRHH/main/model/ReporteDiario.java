package com.SaasRRHH.main.model;

import com.SaasRRHH.main.entity.Empleado;
import com.SaasRRHH.main.entity.TareaAsignada;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "reportes_diarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDiario {

    public enum EstadoReporte {
        PENDIENTE, VALIDADO, OBSERVADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tarea_id", nullable = false)
    @NotNull(message = "La tarea es obligatoria")
    private TareaAsignada tarea;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    @NotNull(message = "El empleado es obligatorio")
    private Empleado empleado;

    @Size(max = 1000)
    private String descripcionTrabajador;

    @Size(max = 1000)
    private String observacionSupervisor;

    @DecimalMin("0.00")
    @DecimalMax("100.00")
    private BigDecimal porcentajeAvance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private EstadoReporte estado = EstadoReporte.PENDIENTE;

    private LocalDateTime fechaReporte;
}
