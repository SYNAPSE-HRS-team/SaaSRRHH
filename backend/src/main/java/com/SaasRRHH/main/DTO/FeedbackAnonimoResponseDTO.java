package com.SaasRRHH.main.DTO;

import com.SaasRRHH.main.model.FeedbackAnonimo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FeedbackAnonimoResponseDTO {
    private Long id;
    private String mensaje;
    private FeedbackAnonimo.CategoriaFeedback categoria;
    private FeedbackAnonimo.EstadoFeedback estado;
    private LocalDateTime fechaEnvio;
}
