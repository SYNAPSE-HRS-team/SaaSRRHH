package com.SaasRRHH.main.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioResponseDTO {

    private Long id;
    private String email;

    private Long rolId;
    private String rolNombre;

    private Boolean activo;

    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimoAcceso;
}