package com.SaasRRHH.main.model;

import jakarta.persistence.*;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;




@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "familiares", indexes = {
                @Index(name = "idx_familiar_empleado", columnList = "empleado_id")
})
public class Familiar {

        public enum Parentesco {
                HIJO, CONYUGE, PADRE, MADRE
        }

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotNull(message = "El empleado es obligatorio")
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "empleado_id", nullable = false, foreignKey = @ForeignKey(name = "fk_familiar_empleado"))
        private Empleado empleado;

        @NotNull(message = "El parentesco es obligatorio")
        @Enumerated(EnumType.STRING)
        @Column(name = "parentesco", nullable = false, length = 50)
        private Parentesco parentesco;

        @NotBlank(message = "El nombre del familiar es obligatorio")
        @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
        @Column(name = "nombres", nullable = false, length = 200)
        private String nombres;

        @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe contener exactamente 8 dígitos numéricos")
        @Column(name = "dni_familiar", length = 8)
        private String dniFamiliar;

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser en el pasado")
        @Column(name = "fecha_nacimiento", nullable = false)
        private LocalDate fechaNacimiento;

        @Column(name = "estudia", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
        private Boolean estudia = false;

        @Column(name = "activo", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
        private Boolean activo = true;

}