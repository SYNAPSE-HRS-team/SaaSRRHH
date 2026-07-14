package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class EmpleadoResponseDTO {

    private Long id;
    private Long usuarioId;
    private String email;
    private String nombres;
    private String apellidos;
    private String dni;
    private String fotoPerfilUrl;
    private BigDecimal sueldoBase;
    private Boolean asignacionFamiliar;
    private LocalDate fechaInicioContrato;
    private LocalDate fechaFinContrato;
    private String cargo;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    
    // ✅ NUEVOS CAMPOS: HORARIO LABORAL CONFIGURABLE
    private LocalTime horaEntrada;
    private LocalTime horaSalida;
    private String diasLaborables;
    private Integer toleranciaMinutos;
    
    // ✅ NUEVOS CAMPOS: TIPO DE PAGO
    private String tipoPago;
    private BigDecimal montoPago;
    
    // ✅ NUEVOS CAMPOS: MÉTRICAS ACTUALES (para el dashboard)
    private String ultimoNivelRiesgo; // BAJO, MEDIO, ALTO
    private Double indicePuntualidad;
    private Integer totalFaltasMes;
    private Integer totalTardanzasMes;
}