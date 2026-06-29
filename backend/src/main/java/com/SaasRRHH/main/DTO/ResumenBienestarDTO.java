package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumenBienestarDTO {

    private Double promedioCargaLaboral;
    private Double promedioApoyoEquipo;
    private Double promedioProyeccion;
    private Double promedioGeneral;
    private Long totalEncuestas;
    private Long empleadosCriticos;
    private String pulsoOrganizacional;
}
