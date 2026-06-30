package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AccesoUsuarioRequestDTO {
    private Long idAcceso;

    private Long usuarioId;

    private LocalDateTime fechaLogin;

    private LocalDateTime fechaLogout;

    private String userAgent;
    
    private Boolean exitoso;
}
