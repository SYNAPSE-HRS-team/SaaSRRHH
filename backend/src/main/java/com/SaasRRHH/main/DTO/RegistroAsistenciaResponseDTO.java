package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class RegistroAsistenciaResponseDTO {
    private Long id;
    private Long empleadoId;
    private Long dispositivoId;
    private LocalDateTime fechaHora;
    private String tipoMarcacion;
    private String metodo;
    private String estado;
    private String observaciones;
    
    // ✅ NUEVOS CAMPOS: CONTROL DE TARDANZAS Y FALTAS
    private Integer minutosTardanza;
    private Boolean esFalta;
    private Boolean justificado;
    private String motivoJustificacion;
    
    // ✅ NUEVOS CAMPOS: INFO DEL EMPLEADO (para listados)
    private String nombreEmpleado;
    private String dniEmpleado;
}