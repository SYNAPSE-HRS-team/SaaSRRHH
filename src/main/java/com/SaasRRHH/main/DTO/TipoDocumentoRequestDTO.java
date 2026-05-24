package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipoDocumentoRequestDTO {

    private String nombre;

    private Boolean obligatorio;

    private Integer diasVigencia;

    private Boolean requiereRenovacion;

    private String descripcion;
}