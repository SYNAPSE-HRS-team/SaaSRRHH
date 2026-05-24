package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DispositivoAutorizadoRequestDTO {
    private Long id;
    private Long usuarioId;
    private String hardwareId;
    private String fcmToken;
    private Boolean activo;
}
