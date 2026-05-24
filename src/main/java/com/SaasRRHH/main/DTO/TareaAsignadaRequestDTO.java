package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TareaAsignadaRequestDTO {
    private Long id;
    private Long empleadoId;
    private Long supervisorId;
    private Long areaId;
    private String funcion;
    private LocalDate fecha;
    private String descripcion;
    private String estado;
}
