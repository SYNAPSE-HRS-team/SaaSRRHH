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

    // ============================================
    // ✅ NUEVOS CAMPOS: RELACIÓN CON EMPLEADO Y RESPUESTA
    // ============================================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", foreignKey = @ForeignKey(name = "fk_feedback_empleado"))
    private Empleado empleado; // Ahora el feedback está vinculado a un empleado
    
    @Column(name = "es_anonimo")
    private Boolean esAnonimo = true; // true = no muestra nombre al admin
    
    @Column(name = "respuesta", length = 1000)
    private String respuesta; // Respuesta del admin
    
    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta; // Cuándo respondió el admin

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
        PENDIENTE,   // Recién enviado
        REVISADO,    // Admin lo leyó y respondió
        NO_PROCEDE,  // Admin determina que no aplica
        ACEPTADO     // Admin acepta y toma acción
    }
}