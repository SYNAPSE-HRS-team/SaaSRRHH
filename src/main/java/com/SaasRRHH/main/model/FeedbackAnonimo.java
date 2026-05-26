package com.SaasRRHH.main.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedback_anonimo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackAnonimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mensaje", length = 1000, nullable = false)
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false, length = 50)
    private CategoriaFeedback categoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoFeedback estado = EstadoFeedback.PENDIENTE;

    @Column(name = "fecha_envio", nullable = false, updatable = false)
    private LocalDateTime fechaEnvio;

    @PrePersist
    public void prePersist() {
        if (fechaEnvio == null)
            fechaEnvio = LocalDateTime.now();
    }

    public enum CategoriaFeedback {
        CLIMA_LABORAL,
        CARGA_TRABAJO,
        LIDERAZGO,
        CONDICIONES_FISICAS,
        COMUNICACION,
        OTRO
    }

    public enum EstadoFeedback {
        PENDIENTE,
        REVISADO,
        RESUELTO
    }
}
