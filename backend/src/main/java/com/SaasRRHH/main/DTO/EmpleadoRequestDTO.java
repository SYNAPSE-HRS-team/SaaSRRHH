package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class EmpleadoRequestDTO {

    private Long usuarioId;
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
    
    // ✅ NUEVOS CAMPOS: HORARIO LABORAL CONFIGURABLE
    private LocalTime horaEntrada;
    private LocalTime horaSalida;
    private String diasLaborables; // "LUN,MAR,MIE,JUE,VIE"
    private Integer toleranciaMinutos;
    
    // ✅ NUEVOS CAMPOS: TIPO DE PAGO
    private String tipoPago; // HORA, DIA, MENSUAL
    private BigDecimal montoPago;
}