package com.SaasRRHH.main.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "metricas_burnout", indexes = {
        @Index(name = "idx_metrica_empleado", columnList = "empleado_id"),
        @Index(name = "idx_burnout_fecha", columnList = "fecha_evaluacion")
})

public class MetricaBurnout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Muchas métricas pueden pertenecer a un empleado
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false, foreignKey = @ForeignKey(name = "fk_burnout_empleado"))
    private Empleado empleado;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_riesgo", nullable = false, length = 10)
    private NivelRiesgoBurnout nivelRiesgo = NivelRiesgoBurnout.BAJO;

    @Column(name = "horas_extra_acumuladas", nullable = false)
    private Integer horasExtraAcumuladas = 0;

    @Column(name = "tendencia_tardanza", nullable = false)
    private Boolean tendenciaTardanza = false;

    @Column(name = "fecha_evaluacion", nullable = false, updatable = false)
    private LocalDateTime fechaEvaluacion;

    public MetricaBurnout() {
    }

    @PrePersist
    public void prePersist() {
        if (fechaEvaluacion == null) {
            fechaEvaluacion = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public NivelRiesgoBurnout getNivelRiesgo() {
        return nivelRiesgo;
    }

    public void setNivelRiesgo(NivelRiesgoBurnout nivelRiesgo) {
        this.nivelRiesgo = nivelRiesgo;
    }

    public Integer getHorasExtraAcumuladas() {
        return horasExtraAcumuladas;
    }

    public void setHorasExtraAcumuladas(Integer horasExtraAcumuladas) {
        this.horasExtraAcumuladas = horasExtraAcumuladas;
    }

    public Boolean getTendenciaTardanza() {
        return tendenciaTardanza;
    }

    public void setTendenciaTardanza(Boolean tendenciaTardanza) {
        this.tendenciaTardanza = tendenciaTardanza;
    }

    public LocalDateTime getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    public void setFechaEvaluacion(LocalDateTime fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }

    public enum NivelRiesgoBurnout {
        BAJO,
        MEDIO,
        ALTO
    }
}
