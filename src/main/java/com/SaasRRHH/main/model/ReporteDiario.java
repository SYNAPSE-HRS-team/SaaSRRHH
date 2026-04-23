package com.SaasRRHH.main.model; 
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
        name = "reportes_diarios",
        indexes = {
                @Index(name = "idx_tarea", columnList = "tarea_id"),
                @Index(name = "idx_empleado", columnList = "empleado_id")
        }
)
public class ReporteDiario {

    /** CHECK: estado IN ('PENDIENTE','VALIDADO','OBSERVADO') */
    public enum EstadoReporte {
        PENDIENTE, VALIDADO, OBSERVADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La tarea es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "tarea_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_reporte_tarea")
    )
    private TareaAsignada tarea;

    @NotNull(message = "El empleado es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "empleado_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_reporte_empleado")
    )
    private Empleado empleado;

    @Size(max = 1000, message = "La descripción del trabajador no puede superar 1000 caracteres")
    @Column(name = "descripcion_trabajador", length = 1000)
    private String descripcionTrabajador;

    @Size(max = 1000, message = "La observación del supervisor no puede superar 1000 caracteres")
    @Column(name = "observacion_supervisor", length = 1000)
    private String observacionSupervisor;

    @DecimalMin(value = "0.00", message = "El porcentaje no puede ser negativo")
    @DecimalMax(value = "100.00", message = "El porcentaje no puede superar 100")
    @Column(
            name = "porcentaje_avance",
            precision = 5,
            scale = 2,
            columnDefinition = "DECIMAL(5,2) DEFAULT 0.00"
    )
    private BigDecimal porcentajeAvance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado",
            length = 20,
            columnDefinition = "VARCHAR(20) DEFAULT 'PENDIENTE'"
    )
    private EstadoReporte estado = EstadoReporte.PENDIENTE;

    @Column(
            name = "fecha_reporte",
            nullable = false,
            insertable = false,
            updatable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP"
    )
    private LocalDateTime fechaReporte;
}