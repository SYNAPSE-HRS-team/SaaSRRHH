package com.SaasRRHH.main.model;

import com.SaasRRHH.main.entity.AreaTrabajo;
import com.SaasRRHH.main.entity.Empleado;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

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
@Table(name = "reportes_incidentes", indexes = {
                @Index(name = "idx_empleado", columnList = "empleado_id"),
                @Index(name = "idx_supervisor", columnList = "supervisor_id"),
                @Index(name = "idx_tarea", columnList = "tarea_id"),
                @Index(name = "idx_area", columnList = "area_id"),
                @Index(name = "idx_fecha", columnList = "fecha_incidente")
})
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

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotNull(message = "El empleado es obligatorio")
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "empleado_id", nullable = false, foreignKey = @ForeignKey(name = "fk_incidente_empleado"))
        private Empleado empleado;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "supervisor_id", foreignKey = @ForeignKey(name = "fk_incidente_supervisor"))
        private Empleado supervisor;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "tarea_id", foreignKey = @ForeignKey(name = "fk_incidente_tarea"))
        private TareaAsignada tarea;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "area_id", foreignKey = @ForeignKey(name = "fk_incidente_area"))
        private AreaTrabajo area;

        @NotNull(message = "El tipo de incidente es obligatorio")
        @Enumerated(EnumType.STRING)
        @Column(name = "tipo", nullable = false, length = 30)
        private TipoIncidente tipo;

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 1000, message = "La descripción no puede superar 1000 caracteres")
        @Column(name = "descripcion", nullable = false, length = 1000)
        private String descripcion;

        @Size(max = 500, message = "La evidencia no puede superar 500 caracteres")
        @Column(name = "evidencia_url", length = 500)
        private String evidenciaUrl;

        @Enumerated(EnumType.STRING)
        @Column(name = "nivel_riesgo", length = 20)
        private NivelRiesgo nivelRiesgo;

        @Enumerated(EnumType.STRING)
        @Column(name = "estado", length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'REPORTADO'")
        private EstadoIncidente estado = EstadoIncidente.REPORTADO;

        @NotNull(message = "La fecha del incidente es obligatoria")
        @Column(name = "fecha_incidente", nullable = false)
        private LocalDateTime fechaIncidente;

        @Column(name = "fecha_registro", nullable = false, insertable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
        private LocalDateTime fechaRegistro;
}