package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.MetricaBurnoutResponseDTO;
import com.SaasRRHH.main.services.MetricaBurnoutService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/burnout")
public class MetricaBurnoutController {

    private final MetricaBurnoutService metricaBurnoutService;

    public MetricaBurnoutController(MetricaBurnoutService metricaBurnoutService) {
        this.metricaBurnoutService = metricaBurnoutService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<MetricaBurnoutResponseDTO>> listarMetricas() {
        return ResponseEntity.ok(metricaBurnoutService.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<MetricaBurnoutResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(metricaBurnoutService.obtenerPorId(id));
    }

    @GetMapping("/empleado/{empleadoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<MetricaBurnoutResponseDTO>> buscarPorEmpleado(@PathVariable Long empleadoId) {
        return ResponseEntity.ok(metricaBurnoutService.buscarPorEmpleado(empleadoId));
    }

    @GetMapping("/empleado/{empleadoId}/ultimo")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Map<String, String>> obtenerUltimoNivelRiesgo(@PathVariable Long empleadoId) {
        String nivel = metricaBurnoutService.obtenerUltimoNivelRiesgo(empleadoId);
        return ResponseEntity.ok(Map.of("empleadoId", empleadoId.toString(), "nivelRiesgo", nivel));
    }

    @GetMapping("/empleado/{empleadoId}/historial")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<MetricaBurnoutResponseDTO>> obtenerHistorial(@PathVariable Long empleadoId) {
        return ResponseEntity.ok(metricaBurnoutService.obtenerHistorialCompleto(empleadoId));
    }

    @PostMapping("/calcular/{empleadoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MetricaBurnoutResponseDTO> calcular(@PathVariable Long empleadoId) {
        MetricaBurnoutResponseDTO resultado = metricaBurnoutService.calcularMetrica(empleadoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    @PostMapping("/recalcular-todas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MetricaBurnoutResponseDTO>> recalcularTodas() {
        List<MetricaBurnoutResponseDTO> resultados = metricaBurnoutService.recalcularTodas();
        return ResponseEntity.ok(resultados);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        metricaBurnoutService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/alertas")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<MetricaBurnoutResponseDTO>> obtenerAlertas() {
        List<MetricaBurnoutResponseDTO> todas = metricaBurnoutService.listar();
        List<MetricaBurnoutResponseDTO> alertas = todas.stream()
                .filter(m -> "ALTO".equals(m.getNivelRiesgo()))
                .toList();
        return ResponseEntity.ok(alertas);
    }

    @GetMapping("/resumen")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> obtenerResumen() {
        List<MetricaBurnoutResponseDTO> todas = metricaBurnoutService.listar();
        long totalAlto = todas.stream().filter(m -> "ALTO".equals(m.getNivelRiesgo())).count();
        long totalMedio = todas.stream().filter(m -> "MEDIO".equals(m.getNivelRiesgo())).count();
        long totalBajo = todas.stream().filter(m -> "BAJO".equals(m.getNivelRiesgo())).count();
        double promedioPuntualidad = todas.stream()
                .filter(m -> m.getIndicePuntualidad() != null)
                .mapToDouble(MetricaBurnoutResponseDTO::getIndicePuntualidad)
                .average().orElse(100.0);
        return ResponseEntity.ok(Map.of(
            "totalEvaluaciones", todas.size(), "riesgoAlto", totalAlto,
            "riesgoMedio", totalMedio, "riesgoBajo", totalBajo,
            "promedioPuntualidad", Math.round(promedioPuntualidad * 100.0) / 100.0
        ));
    }

    @GetMapping("/patrones-detectados")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MetricaBurnoutResponseDTO>> obtenerPatronesDetectados() {
        List<MetricaBurnoutResponseDTO> todas = metricaBurnoutService.listar();
        List<MetricaBurnoutResponseDTO> conPatron = todas.stream()
                .filter(m -> m.getPatronDetectado() != null && !m.getPatronDetectado().isEmpty())
                .toList();
        return ResponseEntity.ok(conPatron);
    }

    @PostMapping("/recalcular/{empleadoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> recalcularParaEmpleado(@PathVariable Long empleadoId) {
        MetricaBurnoutResponseDTO metrica = metricaBurnoutService.calcularMetrica(empleadoId);
        return ResponseEntity.ok(Map.of("mensaje", "Métrica recalculada exitosamente", "metrica", metrica));
    }
}