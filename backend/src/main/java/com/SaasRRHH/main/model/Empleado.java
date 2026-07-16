package com.SaasRRHH.main.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    
    @Column(name = "totp_secret", length = 64, unique = true)
    private String totpSecret;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    // ============================================
    // ✅ NUEVOS CAMPOS: HORARIO LABORAL CONFIGURABLE
    // ============================================
    
    @Column(name = "hora_entrada")
    private LocalTime horaEntrada = LocalTime.of(8, 0); // Default 8:00 AM
    
    @Column(name = "hora_salida")
    private LocalTime horaSalida = LocalTime.of(17, 0); // Default 5:00 PM
    
    @Column(name = "dias_laborables", length = 50)
    private String diasLaborables = "LUN,MAR,MIE,JUE,VIE"; // Días separados por coma
    
    @Column(name = "tolerancia_minutos")
    private Integer toleranciaMinutos = 10; // Minutos de tolerancia para tardanza
    
    @Column(name = "tipo_pago", length = 20)
    private String tipoPago = "MENSUAL"; // HORA, DIA, MENSUAL
    
    @Column(name = "monto_pago", precision = 10, scale = 2)
    private BigDecimal montoPago; // Monto según tipo de pago (por hora, día o mes)

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

    // Métodos de conveniencia
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
    
    // ============================================
    // ✅ NUEVOS MÉTODOS DE UTILIDAD
    // ============================================
    
    public boolean esDiaLaborable(java.time.DayOfWeek dia) {
        if (diasLaborables == null || diasLaborables.isBlank()) return true;
        String diaStrEs;
        switch (dia) {
            case MONDAY: diaStrEs = "LUN"; break;
            case TUESDAY: diaStrEs = "MAR"; break;
            case WEDNESDAY: diaStrEs = "MIE"; break;
            case THURSDAY: diaStrEs = "JUE"; break;
            case FRIDAY: diaStrEs = "VIE"; break;
            case SATURDAY: diaStrEs = "SAB"; break;
            case SUNDAY: diaStrEs = "DOM"; break;
            default: diaStrEs = "";
        }
        String diaStrEn = dia.name().substring(0, 3).toUpperCase(); // MON, TUE, WED...
        String[] dias = diasLaborables.split(",");
        for (String d : dias) {
            String cleanD = d.trim().toUpperCase();
            if (cleanD.equals(diaStrEs) || cleanD.equals(diaStrEn)) {
                return true;
            }
        }
        return false;
    }
    
    // Calcula horas de contrato por día
    public long horasContratoPorDia() {
        if (horaEntrada == null || horaSalida == null) return 8;
        return java.time.Duration.between(horaEntrada, horaSalida).toHours();
    }
}