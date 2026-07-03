package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReporteIncidenteResponseDTO {

    private Long id;

    // ✅ IDs (para compatibilidad)
    private Long empleadoId;
    private Long supervisorId;
    private Long tareaId;
    private Long areaId;

    // ✅ OBJETOS COMPLETOS
    private EmpleadoResponseDTO empleado;
    private EmpleadoResponseDTO supervisor;
    private TareaAsignadaResponseDTO tarea;
    private AreaTrabajoResponseDTO area;

    private String tipo;
    private String descripcion;
    private String evidenciaUrl;
    private String nivelRiesgo;
    private String estado;
    private LocalDateTime fechaIncidente;
    private LocalDateTime fechaRegistro;
}