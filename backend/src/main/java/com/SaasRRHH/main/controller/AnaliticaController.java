package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.DashboardDTO;
import com.SaasRRHH.main.services.AnaliticaService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analitica")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnaliticaController {

    private final AnaliticaService analiticaService;

    // ============================================
    // ENDPOINT ORIGINAL
    // ============================================

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> obtenerDashboard() {
        DashboardDTO dashboard = analiticaService.obtenerDashboard();
        return ResponseEntity.ok(dashboard);
    }

    // ============================================
    // ✅ NUEVOS ENDPOINTS
    // ============================================

    /**
     * ✅ Obtiene el ranking de empleados con bajo desempeño
     */
    @GetMapping("/ranking-desempeno")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<DashboardDTO.EmpleadoAlertaDTO>> obtenerRankingDesempeno() {
        DashboardDTO dashboard = analiticaService.obtenerDashboard();
        return ResponseEntity.ok(dashboard.getRankingBajoDesempeno());
    }

    /**
     * ✅ Obtiene las alertas activas del sistema
     */
    @GetMapping("/alertas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> obtenerAlertas() {
        DashboardDTO dashboard = analiticaService.obtenerDashboard();
        return ResponseEntity.ok(Map.of(
            "empleadosRiesgoAlto", dashboard.getEmpleadosRiesgoAlto(),
            "totalAlertas", dashboard.getTotalAlertas(),
            "feedbackPendientes", dashboard.getFeedbackPendientes(),
            "totalFaltasHoy", dashboard.getTotalFaltasHoy(),
            "totalTardanzasHoy", dashboard.getTotalTardanzasHoy(),
            "promedioPuntualidad", dashboard.getPromedioPuntualidad()
        ));
    }

    /**
     * ✅ Obtiene métricas de puntualidad general
     */
    @GetMapping("/puntualidad")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> obtenerMetricasPuntualidad() {
        DashboardDTO dashboard = analiticaService.obtenerDashboard();
        return ResponseEntity.ok(Map.of(
            "promedioPuntualidad", dashboard.getPromedioPuntualidad(),
            "porcentajeAusentismo", dashboard.getPorcentajeAusentismo(),
            "totalFaltasHoy", dashboard.getTotalFaltasHoy(),
            "totalTardanzasHoy", dashboard.getTotalTardanzasHoy(),
            "totalAusencias", dashboard.getAusencias()
        ));
    }

    /**
     * ✅ Obtiene resumen completo para el dashboard del admin
     */
    @GetMapping("/resumen-completo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardDTO> obtenerResumenCompleto() {
        return ResponseEntity.ok(analiticaService.obtenerDashboard());
    }
}