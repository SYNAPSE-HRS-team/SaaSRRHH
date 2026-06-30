package com.SaasRRHH.main.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDTO {

    // Indicadores laborales
    private long totalEmpleados;

    private long totalUsuarios;

    // Productividad
    private long reportesDiarios;

    // Ausentismo
    private long ausencias;

    // Seguridad
    private long incidentes;

    // Métricas analíticas
    private double porcentajeAusentismo;

    private String nivelRiesgo;
}