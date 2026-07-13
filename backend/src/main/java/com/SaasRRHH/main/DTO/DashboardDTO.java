package com.SaasRRHH.main.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDTO {

    // Indicadores laborales existentes
    private long totalEmpleados;
    private long totalUsuarios;
    private long reportesDiarios;
    private long ausencias;
    private long incidentes;
    private double porcentajeAusentismo;
    private String nivelRiesgo;
    
    // ✅ NUEVOS CAMPOS: MÉTRICAS AVANZADAS
    private long empleadosRiesgoAlto; // Cantidad de empleados con nivel ALTO
    private long totalAlertas; // Total de alertas activas
    private double promedioPuntualidad; // Promedio general de puntualidad (0-100)
    private long totalFaltasHoy; // Faltas del día actual
    private long totalTardanzasHoy; // Tardanzas del día actual
    
    // ✅ NUEVOS CAMPOS: RANKING
    private List<EmpleadoAlertaDTO> rankingBajoDesempeno; // Top empleados problemáticos
    
    // ✅ NUEVOS CAMPOS: FEEDBACK
    private long feedbackPendientes; // Cantidad de feedback sin responder
    
    // Inner class para el ranking
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmpleadoAlertaDTO {
        private Long empleadoId;
        private String nombreEmpleado;
        private String cargo;
        private String nivelRiesgo;
        private Double indicePuntualidad;
        private Integer faltasMes;
        private Integer tardanzasMes;
        private String patronDetectado;
    }
}