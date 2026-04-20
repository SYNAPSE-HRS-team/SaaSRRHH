package com.SaasRRHH.main.model;

import com.SaasRRHH.main.entity.Empleado;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for MetadatoEmpleado - Non-persistent model
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetadatoEmpleado {

    private Long id;

    @NotNull(message = "El empleado es obligatorio")
    private Empleado empleado;

    @NotBlank(message = "La clave es obligatoria")
    @Size(max = 50, message = "La clave no puede superar 50 caracteres")
    private String clave;

    @Size(max = 250, message = "El valor no puede superar 250 caracteres")
    private String valor;
}
