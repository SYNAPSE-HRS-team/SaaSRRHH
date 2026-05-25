package com.SaasRRHH.main.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "encuestas_bienestar", uniqueConstraints = {
                @UniqueConstraint(name = "uq_encuesta", columnNames = { "empleado_id", "fecha" })
}, indexes = {
                @Index(name = "idx_empleado", columnList = "empleado_id"),
                @Index(name = "idx_fecha", columnList = "fecha")
})
public class Encuestabienestar {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotNull(message = "El empleado es obligatorio")
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "empleado_id", nullable = false, foreignKey = @ForeignKey(name = "fk_encuesta_empleado"))
        private Empleado empleado;

        @NotNull(message = "La fecha de la encuesta es obligatoria")
        @Column(name = "fecha", nullable = false)
        private LocalDate fecha;

        @Min(value = 1, message = "Debe ser entre 1 y 5")
        @Max(value = 5, message = "Debe ser entre 1 y 5")
        @Column(name = "carga_laboral")
        private Integer cargaLaboral;

        @Min(value = 1, message = "Debe ser entre 1 y 5")
        @Max(value = 5, message = "Debe ser entre 1 y 5")
        @Column(name = "apoyo_equipo")
        private Integer apoyoEquipo;

        @Min(value = 1, message = "Debe ser entre 1 y 5")
        @Max(value = 5, message = "Debe ser entre 1 y 5")
        @Column(name = "proyeccion")
        private Integer proyeccion;
}