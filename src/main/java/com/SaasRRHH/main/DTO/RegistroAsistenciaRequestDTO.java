package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RegistroAsistenciaRequestDTO {
    private Long id;
    private Long empleadoId;
    private Long dispositivoId;
    private LocalDateTime fechaHora;
    private String tipoMarcacion;
    private String metodo;
    private String estado;
    private String observaciones;
}
