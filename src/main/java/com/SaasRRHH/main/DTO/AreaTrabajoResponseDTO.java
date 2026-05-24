package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AreaTrabajoResponseDTO {
    private Long id;
    
    private String nombre;

    private String cultivoTipo;

    private Boolean activo;

    private LocalDateTime fechaRegistro;

}
