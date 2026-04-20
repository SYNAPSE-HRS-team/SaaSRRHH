package com.SaasRRHH.main.model;

import com.SaasRRHH.main.entity.Empleado;
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
public class Familiar {

    public enum Parentesco {
        HIJO, CONYUGE, PADRE, MADRE
    }

    private Long id;

    @NotNull(message = "El empleado es obligatorio")
    private Empleado empleado;

    @NotNull(message = "El parentesco es obligatorio")
    private Parentesco parentesco;

    @NotBlank(message = "El nombre del familiar es obligatorio")
    @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
    private String nombres;

    @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe contener exactamente 8 dígitos numéricos")
    private String dniFamiliar;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;

    private Boolean estudia = false;

    private Boolean activo = true;
}
