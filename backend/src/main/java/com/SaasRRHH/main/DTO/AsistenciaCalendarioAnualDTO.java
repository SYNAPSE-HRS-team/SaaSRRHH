package com.SaasRRHH.main.DTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
public class AsistenciaCalendarioAnualDTO {
    private Integer anio;
    private List<AsistenciaCalendarioMesDTO> meses;
}