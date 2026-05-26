package com.SaasRRHH.main.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "empleados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    @Column(name = "nombres", length = 100, nullable = false)
    private String nombres;

    @Column(name = "apellidos", length = 100, nullable = false)
    private String apellidos;

    @Column(name = "dni", length = 8, nullable = false, unique = true)
    private String dni;

    @Column(name = "foto_perfil_url", length = 500)
    private String fotoPerfilUrl;

    @Column(name = "sueldo_base", precision = 10, scale = 2)
    private BigDecimal sueldoBase;

    @Column(name = "asignacion_familiar")
    private Boolean asignacionFamiliar = false;

    @Column(name = "fecha_inicio_contrato", nullable = false)
    private LocalDate fechaInicioContrato;

    @Column(name = "fecha_fin_contrato")
    private LocalDate fechaFinContrato;

    @Column(name = "cargo", length = 100)
    private String cargo;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    // ==========================
    // RELACION CON METRICAS BURNOUT
    // ==========================
    @JsonIgnore
    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MetricaBurnout> metricasBurnout = new ArrayList<>();

    // ==========================
    // RELACION CON ENCUESTAS BIENESTAR
    // ==========================
    @JsonIgnore
    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Encuestabienestar> encuestasBienestar = new ArrayList<>();

    // Métodos de conveniencia (muy recomendables)
    public void agregarMetricaBurnout(MetricaBurnout metrica) {
        metricasBurnout.add(metrica);
        metrica.setEmpleado(this);
    }

    public void removerMetricaBurnout(MetricaBurnout metrica) {
        metricasBurnout.remove(metrica);
        metrica.setEmpleado(null);
    }

    public void agregarEncuestaBienestar(Encuestabienestar encuesta) {
        encuestasBienestar.add(encuesta);
        encuesta.setEmpleado(this);
    }

    public void removerEncuestaBienestar(Encuestabienestar encuesta) {
        encuestasBienestar.remove(encuesta);
        encuesta.setEmpleado(null);
    }
}