package com.SaasRRHH.main.model;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
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
@Table(name = "tareas_asignadas", indexes = {
                @Index(name = "idx_empleado_fecha", columnList = "empleado_id, fecha"),
                @Index(name = "idx_supervisor", columnList = "supervisor_id"),
                @Index(name = "idx_area", columnList = "area_id")
})
public class TareaAsignada {

        /**
         * CHECK: funcion IN ('CULTIVADOR','ROCIADOR','ARADOR','RECOLECTOR','LIMPIADOR')
         */
        public enum Funcion {
                CULTIVADOR, ROCIADOR, ARADOR, RECOLECTOR, LIMPIADOR
        }

        /** CHECK: estado IN ('PENDIENTE','EN_PROGRESO','COMPLETADO','CANCELADO') */
        public enum EstadoTarea {
                PENDIENTE, EN_PROGRESO, COMPLETADO, CANCELADO
        }

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotNull(message = "El empleado es obligatorio")
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "empleado_id", nullable = false, foreignKey = @ForeignKey(name = "fk_tarea_empleado"))
        private Empleado empleado;

        @NotNull(message = "El supervisor es obligatorio")
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "supervisor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_tarea_supervisor"))
        private Empleado supervisor;

        @NotNull(message = "El área es obligatoria")
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "area_id", nullable = false, foreignKey = @ForeignKey(name = "fk_tarea_area"))
        private AreaTrabajo area;

        @NotNull(message = "La función es obligatoria")
        @Enumerated(EnumType.STRING)
        @Column(name = "funcion", nullable = false, length = 30)
        private Funcion funcion;

        @NotNull(message = "La fecha es obligatoria")
        @Column(name = "fecha", nullable = false)
        private LocalDate fecha;

        @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
        @Column(name = "descripcion", length = 500)
        private String descripcion;

        @Enumerated(EnumType.STRING)
        @Column(name = "estado", length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'PENDIENTE'")
        private EstadoTarea estado = EstadoTarea.PENDIENTE;

        @Column(name = "fecha_registro", nullable = false, insertable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
        private LocalDateTime fechaRegistro;
}