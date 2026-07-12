package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class FeedbackAnonimoResponseDTO {
    private Long id;
    private String mensaje;
    private String categoria;
    private String estado; // PENDIENTE, REVISADO, NO_PROCEDE, ACEPTADO
    private LocalDateTime fechaEnvio;
    
    // ✅ NUEVOS CAMPOS
    private Long empleadoId;
    private String nombreEmpleado; // null si es anónimo
    private Boolean esAnonimo;
    private String respuesta;
    private LocalDateTime fechaRespuesta;
}