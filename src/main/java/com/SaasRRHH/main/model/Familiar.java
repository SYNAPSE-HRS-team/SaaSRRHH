package com.SaasRRHH.main.model;

import com.SaasRRHH.main.entity.Empleado;

import jakarta.persistence.Column;
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
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;


@Entity
@Table(name = "familiares")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Familiar {

    public enum Parentesco {
        HIJO, CONYUGE, PADRE, MADRE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    @NotNull(message = "El empleado es obligatorio")
    private Empleado empleado;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "El parentesco es obligatorio")
    private Parentesco parentesco;

    @NotBlank(message = "El nombre del familiar es obligatorio")
    @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
    private String nombres;

    @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe contener exactamente 8 dígitos numéricos")
    @Column(unique = true, length = 8)
    private String dniFamiliar;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;

    private Boolean estudia = false;

    private Boolean activo = true;
}