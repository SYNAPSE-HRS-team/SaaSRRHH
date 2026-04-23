package com.SaasRRHH.main.model;

import com.SaasRRHH.main.model.Usuario;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO for DispositivoAutorizado - Non-persistent model
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DispositivoAutorizado {

    private Long id;

    @NotNull(message = "El usuario es obligatorio")
    private Usuario usuario;

    @NotBlank(message = "El hardware ID es obligatorio")
    @Size(max = 100, message = "El hardware ID no puede superar 100 caracteres")
    private String hardwareId;

    @Size(max = 500, message = "El FCM token no puede superar 500 caracteres")
    private String fcmToken;

    private Boolean activo = true;

    private LocalDateTime fechaRegistro;
}
