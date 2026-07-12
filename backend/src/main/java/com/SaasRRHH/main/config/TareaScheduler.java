package com.SaasRRHH.main.config;

import com.SaasRRHH.main.services.MetricaBurnoutService;
import com.SaasRRHH.main.services.RegistroAsistenciaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TareaScheduler {

    private static final Logger log = LoggerFactory.getLogger(TareaScheduler.class);

    private final RegistroAsistenciaService registroAsistenciaService;
    private final MetricaBurnoutService metricaBurnoutService;

    // ============================================
    // ⏰ TAREAS PROGRAMADAS
    // ============================================

    /**
     * ✅ Marca faltas automáticas todos los días a las 11:00 PM
     * Verifica qué empleados no marcaron entrada y los registra como falta
     */
    @Scheduled(cron = "0 0 23 * * ?")
    public void marcarFaltasDiarias() {
        log.info("⏰ [SCHEDULER] Iniciando marcado automático de faltas diarias...");
        try {
            registroAsistenciaService.procesarFaltasAutomaticas();
            log.info("✅ [SCHEDULER] Faltas diarias procesadas correctamente");
        } catch (Exception e) {
            log.error("❌ [SCHEDULER] Error al marcar faltas diarias: {}", e.getMessage(), e);
        }
    }

    /**
     * ✅ Recalcula métricas de burnout todos los lunes a las 6:00 AM
     * Evalúa el desempeño semanal de todos los empleados
     */
    @Scheduled(cron = "0 0 6 * * MON")
    public void recalcularMetricasSemanales() {
        log.info("⏰ [SCHEDULER] Iniciando recálculo semanal de métricas de burnout...");
        try {
            metricaBurnoutService.recalcularTodas();
            log.info("✅ [SCHEDULER] Métricas de burnout recalculadas exitosamente");
        } catch (Exception e) {
            log.error("❌ [SCHEDULER] Error al recalcular métricas semanales: {}", e.getMessage(), e);
        }
    }

    /**
     * ✅ Envía alertas al admin sobre empleados con alto riesgo
     * Se ejecuta todos los días a las 8:00 AM (antes del inicio de jornada)
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void enviarAlertasAdmin() {
        log.info("⏰ [SCHEDULER] Verificando alertas para administradores...");
        try {
            var metricas = metricaBurnoutService.listar();
            
            long empleadosRiesgoAlto = metricas.stream()
                    .filter(m -> "ALTO".equals(m.getNivelRiesgo()))
                    .count();
            
            long empleadosRiesgoMedio = metricas.stream()
                    .filter(m -> "MEDIO".equals(m.getNivelRiesgo()))
                    .count();
            
            long empleadosConPatron = metricas.stream()
                    .filter(m -> m.getPatronDetectado() != null && !m.getPatronDetectado().isEmpty())
                    .count();
            
            if (empleadosRiesgoAlto > 0) {
                log.warn("🚨 [ALERTA] {} empleados con riesgo ALTO de burnout", empleadosRiesgoAlto);
            }
            
            if (empleadosRiesgoMedio > 0) {
                log.warn("⚠️ [ALERTA] {} empleados con riesgo MEDIO de burnout", empleadosRiesgoMedio);
            }
            
            if (empleadosConPatron > 0) {
                log.warn("🔍 [ALERTA] {} empleados con patrones de tardanza detectados", empleadosConPatron);
            }
            
            // Resumen diario
            log.info("📊 [RESUMEN DIARIO] Riesgo ALTO: {} | Riesgo MEDIO: {} | Patrones: {} | Total evaluados: {}",
                    empleadosRiesgoAlto, empleadosRiesgoMedio, empleadosConPatron, metricas.size());
            
        } catch (Exception e) {
            log.error("❌ [SCHEDULER] Error al enviar alertas: {}", e.getMessage(), e);
        }
    }

    /**
     * ✅ Limpieza de métricas antiguas (opcional, cada domingo a las 3:00 AM)
     * Puedes descomentarlo si necesitas limpiar métricas de más de 6 meses
     */
    // @Scheduled(cron = "0 0 3 * * SUN")
    // public void limpiarMetricasAntiguas() {
    //     log.info("⏰ [SCHEDULER] Iniciando limpieza de métricas antiguas...");
    //     // Implementar lógica de limpieza si es necesario
    // }

    /**
     * ✅ Procesa tardanzas masivas (cada hora durante horario laboral)
     * Detecta automáticamente tardanzas para empleados que ya marcaron entrada
     */
    @Scheduled(cron = "0 30 8,9,10 * * MON-FRI")
    public void procesarTardanzasPeriodicas() {
        log.info("⏰ [SCHEDULER] Verificando tardanzas en horario pico...");
        try {
            registroAsistenciaService.procesarFaltasAutomaticas();
            log.info("✅ [SCHEDULER] Verificación de tardanzas completada");
        } catch (Exception e) {
            log.error("❌ [SCHEDULER] Error al procesar tardanzas: {}", e.getMessage(), e);
        }
    }

    /**
     * ✅ Reporte semanal de bienestar (viernes a las 5:00 PM)
     */
    @Scheduled(cron = "0 0 17 * * FRI")
    public void generarReporteSemanal() {
        log.info("⏰ [SCHEDULER] Generando reporte semanal de bienestar...");
        try {
            var metricas = metricaBurnoutService.listar();
            
            double promedioPuntualidad = metricas.stream()
                    .filter(m -> m.getIndicePuntualidad() != null)
                    .mapToDouble(m -> m.getIndicePuntualidad())
                    .average()
                    .orElse(100.0);
            
            long totalFaltas = metricas.stream()
                    .filter(m -> m.getFaltasPeriodo() != null)
                    .mapToInt(m -> m.getFaltasPeriodo())
                    .sum();
            
            long totalTardanzas = metricas.stream()
                    .filter(m -> m.getTardanzasPeriodo() != null)
                    .mapToInt(m -> m.getTardanzasPeriodo())
                    .sum();
            
            log.info("📊 [REPORTE SEMANAL] Promedio Puntualidad: {}% | Faltas totales: {} | Tardanzas totales: {} | Empleados evaluados: {}",
                    Math.round(promedioPuntualidad * 100.0) / 100.0, totalFaltas, totalTardanzas, metricas.size());
            
        } catch (Exception e) {
            log.error("❌ [SCHEDULER] Error al generar reporte semanal: {}", e.getMessage(), e);
        }
    }
}