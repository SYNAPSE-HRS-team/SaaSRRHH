package com.SaasRRHH.main.DTO;

import com.SaasRRHH.main.model.FeedbackAnonimo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackAnonimoRequestDTO {
    private String mensaje;
    private FeedbackAnonimo.CategoriaFeedback categoria;
}
