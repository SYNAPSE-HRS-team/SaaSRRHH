package com.SaasRRHH.main.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tareas_asignadas")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TareaAsignada {

    public enum Funcion {
        CULTIVADOR, ROCIADOR, ARADOR, RECOLECTOR, LIMPIADOR
    }

    public enum EstadoTarea {
        PENDIENTE, EN_PROGRESO, COMPLETADO, CANCELADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El empleado es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @NotNull(message = "El supervisor es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supervisor_id", nullable = false)
    private Empleado supervisor;

    @NotNull(message = "El área es obligatoria")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id", nullable = false)
    private AreaTrabajo area;

    @NotNull(message = "La función es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(name = "funcion", nullable = false, length = 30)
    private Funcion funcion;

    @NotNull(message = "La fecha es obligatoria")
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Size(max = 500)
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private EstadoTarea estado = EstadoTarea.PENDIENTE;

    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();
}