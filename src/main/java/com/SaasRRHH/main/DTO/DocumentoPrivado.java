package com.SaasRRHH.main.DTO;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.SaasRRHH.main.model.Empleado;

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
    private com.SaasRRHH.main.DTO.TipoDocumento tipo;

    @NotBlank(message = "La URL del archivo es obligatoria")
    @Size(max = 500, message = "La URL no puede superar 500 caracteres")
    private String archivoUrl;

    private LocalDate fechaVencimiento;

    private LocalDateTime fechaCarga;

    private Boolean activo = true;
}
