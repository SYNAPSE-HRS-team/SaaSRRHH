package com.SaasRRHH.main.DTO;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.SaasRRHH.main.model.Usuario;

/**
 * DTO for AccesoUsuario - Non-persistent model
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AccesoUsuario {

    private Long idAcceso;

    @NotNull(message = "El usuario es obligatorio")
    private Usuario usuario;

    private LocalDateTime fechaLogin;

    private LocalDateTime fechaLogout;

    @Size(max = 255, message = "User-agent no puede superar 255 caracteres")
    private String userAgent;

    private Boolean exitoso = true;
}
