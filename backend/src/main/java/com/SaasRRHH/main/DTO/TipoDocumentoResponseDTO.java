package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipoDocumentoResponseDTO {

    private Long idTipo;

    private String nombre;

    private Boolean obligatorio;

    private Integer diasVigencia;

    private Boolean requiereRenovacion;

    private String descripcion;
}