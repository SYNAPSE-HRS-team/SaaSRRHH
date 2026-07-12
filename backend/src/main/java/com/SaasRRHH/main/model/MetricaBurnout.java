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

    // ============================================
    // NUEVOS CAMPOS: MÉTRICAS DETALLADAS
    // ============================================
    
    @Column(name = "faltas_periodo")
    private Integer faltasPeriodo = 0;
    
    @Column(name = "tardanzas_periodo")
    private Integer tardanzasPeriodo = 0;
    
    @Column(name = "patron_detectado", length = 100)
    private String patronDetectado;
    
    @Column(name = "indice_puntualidad")
    private Double indicePuntualidad = 100.0;
    
    @Column(name = "dias_trabajados")
    private Integer diasTrabajados = 0;
    
    @Column(name = "horas_reales")
    private Integer horasReales = 0;
    
    @Column(name = "horas_contrato")
    private Integer horasContrato = 0;

    public MetricaBurnout() {
    }

    @PrePersist
    public void prePersist() {
        if (fechaEvaluacion == null) {
            fechaEvaluacion = LocalDateTime.now();
        }
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
    public NivelRiesgoBurnout getNivelRiesgo() { return nivelRiesgo; }
    public void setNivelRiesgo(NivelRiesgoBurnout nivelRiesgo) { this.nivelRiesgo = nivelRiesgo; }
    public Integer getHorasExtraAcumuladas() { return horasExtraAcumuladas; }
    public void setHorasExtraAcumuladas(Integer horasExtraAcumuladas) { this.horasExtraAcumuladas = horasExtraAcumuladas; }
    public Boolean getTendenciaTardanza() { return tendenciaTardanza; }
    public void setTendenciaTardanza(Boolean tendenciaTardanza) { this.tendenciaTardanza = tendenciaTardanza; }
    public LocalDateTime getFechaEvaluacion() { return fechaEvaluacion; }
    public void setFechaEvaluacion(LocalDateTime fechaEvaluacion) { this.fechaEvaluacion = fechaEvaluacion; }
    public Integer getFaltasPeriodo() { return faltasPeriodo; }
    public void setFaltasPeriodo(Integer faltasPeriodo) { this.faltasPeriodo = faltasPeriodo; }
    public Integer getTardanzasPeriodo() { return tardanzasPeriodo; }
    public void setTardanzasPeriodo(Integer tardanzasPeriodo) { this.tardanzasPeriodo = tardanzasPeriodo; }
    public String getPatronDetectado() { return patronDetectado; }
    public void setPatronDetectado(String patronDetectado) { this.patronDetectado = patronDetectado; }
    public Double getIndicePuntualidad() { return indicePuntualidad; }
    public void setIndicePuntualidad(Double indicePuntualidad) { this.indicePuntualidad = indicePuntualidad; }
    public Integer getDiasTrabajados() { return diasTrabajados; }
    public void setDiasTrabajados(Integer diasTrabajados) { this.diasTrabajados = diasTrabajados; }
    public Integer getHorasReales() { return horasReales; }
    public void setHorasReales(Integer horasReales) { this.horasReales = horasReales; }
    public Integer getHorasContrato() { return horasContrato; }
    public void setHorasContrato(Integer horasContrato) { this.horasContrato = horasContrato; }

    public enum NivelRiesgoBurnout {
        BAJO,
        MEDIO,
        ALTO
    }
}