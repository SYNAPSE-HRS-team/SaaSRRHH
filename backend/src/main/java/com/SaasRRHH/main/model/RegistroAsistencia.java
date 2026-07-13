package com.SaasRRHH.main.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "registros_asistencia")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RegistroAsistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispositivo_id")
    private DispositivoAutorizado dispositivo;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora = LocalDateTime.now();

    @Column(name = "tipo_marcacion", length = 20, nullable = false)
    private String tipoMarcacion; // ENTRADA, SALIDA

    @Column(name = "metodo", length = 20)
    private String metodo = "QR";

    @Column(name = "estado", length = 20)
    private String estado = "VALIDADO"; // VALIDADO, OBSERVADO, RECHAZADO

    @Column(name = "observaciones", length = 500)
    private String observaciones;
    
    // ============================================
    // ✅ NUEVOS CAMPOS: CONTROL DE TARDANZAS Y FALTAS
    // ============================================
    
    @Column(name = "minutos_tardanza")
    private Integer minutosTardanza = 0; // Minutos de retraso (0 si es puntual)
    
    @Column(name = "es_falta")
    private Boolean esFalta = false; // true si no marcó en un día laborable
    
    @Column(name = "justificado")
    private Boolean justificado = false; // true si la falta/tardanza tiene justificación
    
    @Column(name = "motivo_justificacion", length = 500)
    private String motivoJustificacion; // Texto opcional para justificar
}