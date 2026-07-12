package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class MetricaBurnoutResponseDTO {

    private Long id;
    private Long empleadoId;
    private String nombreEmpleado;
    private String nivelRiesgo;
    private Integer horasExtraAcumuladas;
    private Boolean tendenciaTardanza;
    private LocalDateTime fechaEvaluacion;
    private String recomendaciones;
    
    // ✅ NUEVOS CAMPOS: MÉTRICAS DETALLADAS
    private Integer faltasPeriodo;
    private Integer tardanzasPeriodo;
    private String patronDetectado;
    private Double indicePuntualidad;
    private Integer diasTrabajados;
    private Integer horasReales;
    private Integer horasContrato;
    
    // ✅ NUEVOS CAMPOS: INFO ADICIONAL
    private String cargo;
    private String dniEmpleado;
    private LocalDate fechaInicioContrato;  
}