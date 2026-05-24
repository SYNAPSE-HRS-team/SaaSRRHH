package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DispositivoAutorizadoResponseDTO {
    private Long id;
    private Long usuarioId;
    private String hardwareId;
    private String fcmToken;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
}
