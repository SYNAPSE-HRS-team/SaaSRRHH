package com.SaasRRHH.main.DTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public class AsistenciaQrDTO {
    private String payload;
    private Long empleadoId;
    private String empleadoNombre;
    private long segundosRestantes;
    private long expiraEnEpoch;
}