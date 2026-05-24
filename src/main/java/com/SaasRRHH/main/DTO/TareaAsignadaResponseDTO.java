package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class TareaAsignadaResponseDTO {
    private Long id;
    private Long empleadoId;
    private Long supervisorId;
    private Long areaId;
    private String funcion;
    private LocalDate fecha;
    private String descripcion;
    private String estado;
    private LocalDateTime fechaRegistro;
}
