package com.SaasRRHH.main.model;

import com.SaasRRHH.main.entity.Empleado;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for DocumentoPrivado - Non-persistent model
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoPrivado {

    private Long id;

    @NotNull(message = "El empleado es obligatorio")
    private Empleado empleado;

    @NotNull(message = "El tipo de documento es obligatorio")
    private com.SaasRRHH.main.model.TipoDocumento tipo;

    @NotBlank(message = "La URL del archivo es obligatoria")
    @Size(max = 500, message = "La URL no puede superar 500 caracteres")
    private String archivoUrl;

    private LocalDate fechaVencimiento;

    private LocalDateTime fechaCarga;

    private Boolean activo = true;
}
