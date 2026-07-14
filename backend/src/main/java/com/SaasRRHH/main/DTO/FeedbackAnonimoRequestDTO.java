package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackAnonimoRequestDTO {
    private String mensaje;
    private String categoria; // CLIMA_LABORAL, CARGA_TRABAJO, LIDERAZGO, etc.
    
    // ✅ NUEVOS CAMPOS
    private Long empleadoId; // ID del empleado que envía el feedback
    private Boolean esAnonimo; // Si quiere mantenerse anónimo
    private String respuesta; // Para cuando el admin responde
}