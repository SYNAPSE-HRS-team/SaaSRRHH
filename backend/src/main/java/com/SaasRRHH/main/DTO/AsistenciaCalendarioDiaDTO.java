package com.SaasRRHH.main.DTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public class AsistenciaCalendarioDiaDTO {
    private String fecha;
    private String estado;
    private Long asistenciaId;
    private String horaEntrada;
}