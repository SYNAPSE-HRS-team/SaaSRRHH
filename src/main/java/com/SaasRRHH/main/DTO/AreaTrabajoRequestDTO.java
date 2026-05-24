package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AreaTrabajoRequestDTO {
    private Long id;
    private String nombre;
    private String cultivoTipo;
    private Boolean activo;
}
