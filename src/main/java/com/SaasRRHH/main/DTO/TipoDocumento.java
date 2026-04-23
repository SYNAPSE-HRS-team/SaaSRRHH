package com.SaasRRHH.main.DTO; 
import jakarta.validation.constraints.*;
import lombok.Data;



/**
 * DTO for TipoDocumento - Non-persistent model
 */
@Data
public class TipoDocumento {

    private Long idTipo;

    @NotBlank(message = "El nombre del tipo de documento es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar 50 caracteres")
    private String nombre;

    private Boolean obligatorio = false;

    private Integer diasVigencia;

    private Boolean requiereRenovacion = false;

    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    private String descripcion;
}
