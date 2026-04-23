package com.SaasRRHH.main.DTO; 
import com.SaasRRHH.main.model.RegistroAsistencia;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO for ValidacionSeguridad - Non-persistent model
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidacionSeguridadDTO {

    private Long id;

    @NotNull(message = "La asistencia es obligatoria")
    private RegistroAsistencia asistencia;

    private com.SaasRRHH.main.DTO.DispositivoAutorizado dispositivo;

    @Size(max = 255, message = "El TOTP hash no puede superar 255 caracteres")
    private String totpHash;

    private Boolean totpValido = false;

    private LocalDateTime fechaValidacion;
}
