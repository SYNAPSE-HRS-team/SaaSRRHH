package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.MetricaBurnoutResponseDTO;
import com.SaasRRHH.main.services.MetricaBurnoutService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/burnout")
@PreAuthorize("isAuthenticated()")
public class MetricaBurnoutController {

    private final MetricaBurnoutService metricaBurnoutService;

    public MetricaBurnoutController(MetricaBurnoutService metricaBurnoutService) {
        this.metricaBurnoutService = metricaBurnoutService;
    }

    // ==========================
    // ✅ GET ALL - Retorna DTOs
    // ==========================
    @GetMapping
    public ResponseEntity<List<MetricaBurnoutResponseDTO>> listarMetricas() {
        return ResponseEntity.ok(metricaBurnoutService.listar());
    }

    // ==========================
    // ✅ GET BY ID - Retorna DTO
    // ==========================
    @GetMapping("/{id}")
    public ResponseEntity<MetricaBurnoutResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(metricaBurnoutService.obtenerPorId(id));
    }

    // ==========================
    // ✅ GET BY EMPLEADO - Retorna DTOs
    // ==========================
    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<MetricaBurnoutResponseDTO>> buscarPorEmpleado(@PathVariable Long empleadoId) {
        return ResponseEntity.ok(metricaBurnoutService.buscarPorEmpleado(empleadoId));
    }

    // ==========================
    // ✅ GET ÚLTIMO NIVEL DE RIESGO
    // ==========================
    @GetMapping("/empleado/{empleadoId}/ultimo")
    public ResponseEntity<String> obtenerUltimoNivelRiesgo(@PathVariable Long empleadoId) {
        String nivel = metricaBurnoutService.obtenerUltimoNivelRiesgo(empleadoId);
        return ResponseEntity.ok(nivel);
    }

    // ==========================
    // ✅ GET HISTORIAL COMPLETO
    // ==========================
    @GetMapping("/empleado/{empleadoId}/historial")
    public ResponseEntity<List<MetricaBurnoutResponseDTO>> obtenerHistorial(@PathVariable Long empleadoId) {
        return ResponseEntity.ok(metricaBurnoutService.obtenerHistorialCompleto(empleadoId));
    }

    // ==========================
    // ✅ CALCULAR AUTOMÁTICAMENTE
    // ==========================
    @PostMapping("/calcular/{empleadoId}")
    public ResponseEntity<MetricaBurnoutResponseDTO> calcular(@PathVariable Long empleadoId) {
        MetricaBurnoutResponseDTO resultado = metricaBurnoutService.calcularMetrica(empleadoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    // ==========================
    // ✅ RECALCULAR TODOS
    // ==========================
    @PostMapping("/recalcular-todas")
    public ResponseEntity<List<MetricaBurnoutResponseDTO>> recalcularTodas() {
        List<MetricaBurnoutResponseDTO> resultados = metricaBurnoutService.recalcularTodas();
        return ResponseEntity.ok(resultados);
    }

    // ==========================
    // ✅ DELETE
    // ==========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        metricaBurnoutService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}