package com.SaasRRHH.main.DTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
public class AsistenciaCalendarioMesDTO {
    private Integer anio;
    private Integer mes;
    private List<AsistenciaCalendarioDiaDTO> dias;
}