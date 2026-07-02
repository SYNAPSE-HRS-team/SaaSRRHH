package com.SaasRRHH.main.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class BoletaPagoResponseDTO {

    private Long id;

    private Long empleadoId;

    private String empleadoNombre;

    private Long planillaId;

    private Integer planillaMes;

    private Integer planillaAnio;

    private BigDecimal sueldoBase;

    private Integer diasTrabajados;

    private Integer diasNoTrabajados;

    private BigDecimal asignacionFamiliar;

    private BigDecimal bonoBeta;

    private BigDecimal horasExtraPago;

    private BigDecimal otrosBonos;

    private BigDecimal descuentoInasistencia;

    private BigDecimal otrosDescuentos;

    private BigDecimal totalIngresos;

    private BigDecimal totalDescuentos;

    private BigDecimal netoPagar;

    private LocalDateTime fechaEmision;
}