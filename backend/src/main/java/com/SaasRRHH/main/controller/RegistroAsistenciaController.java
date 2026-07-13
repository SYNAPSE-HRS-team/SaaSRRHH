package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.AsistenciaCalendarioAnualDTO;
import com.SaasRRHH.main.DTO.AsistenciaCalendarioMesDTO;
import com.SaasRRHH.main.DTO.AsistenciaQrDTO;
import com.SaasRRHH.main.DTO.RegistroAsistenciaRequestDTO;
import com.SaasRRHH.main.DTO.RegistroAsistenciaResponseDTO;
import com.SaasRRHH.main.services.RegistroAsistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RegistroAsistenciaController {

    private final RegistroAsistenciaService service;

    // ============================================
    // ENDPOINTS ORIGINALES (SIN CAMBIOS)
    // ============================================

    @GetMapping
    public ResponseEntity<List<RegistroAsistenciaResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroAsistenciaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<RegistroAsistenciaResponseDTO> guardar(@RequestBody RegistroAsistenciaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistroAsistenciaResponseDTO> actualizar(
            @PathVariable Long id,
            @RequestBody RegistroAsistenciaRequestDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/entrada/{empleadoId}")
    public ResponseEntity<RegistroAsistenciaResponseDTO> registrarEntrada(
            @PathVariable Long empleadoId,
            @RequestParam(required = false) String metodo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarEntrada(empleadoId, metodo));
    }

    @PostMapping("/salida/{empleadoId}")
    public ResponseEntity<RegistroAsistenciaResponseDTO> registrarSalida(
            @PathVariable Long empleadoId,
            @RequestParam(required = false) String metodo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarSalida(empleadoId, metodo));
    }

    @GetMapping("/mi-qr")
    public ResponseEntity<AsistenciaQrDTO> generarQr() {
        return ResponseEntity.ok(service.generarQrEmpleadoActual());
    }

    @PostMapping("/scan-qr")
    public ResponseEntity<RegistroAsistenciaResponseDTO> scanQr(@RequestBody Map<String, String> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarPorQr(body.get("payload")));
    }

    @GetMapping("/mi-calendario")
    public ResponseEntity<AsistenciaCalendarioMesDTO> miCalendario(
            @RequestParam Integer anio,
            @RequestParam Integer mes) {
        return ResponseEntity.ok(service.calendarioEmpleadoActual(anio, mes));
    }

    @GetMapping("/mi-calendario/anual")
    public ResponseEntity<AsistenciaCalendarioAnualDTO> miCalendarioAnual(@RequestParam Integer anio) {
        return ResponseEntity.ok(service.calendarioAnualEmpleadoActual(anio));
    }

    @GetMapping("/calendario/{empleadoId}")
    public ResponseEntity<AsistenciaCalendarioMesDTO> calendarioEmpleado(
            @PathVariable Long empleadoId,
            @RequestParam Integer anio,
            @RequestParam Integer mes) {
        return ResponseEntity.ok(service.calendarioEmpleado(empleadoId, anio, mes));
    }

    @GetMapping("/calendario/{empleadoId}/anual")
    public ResponseEntity<AsistenciaCalendarioAnualDTO> calendarioAnualEmpleado(
            @PathVariable Long empleadoId,
            @RequestParam Integer anio) {
        return ResponseEntity.ok(service.calendarioAnualEmpleado(empleadoId, anio));
    }

    @GetMapping("/mi-historial")
    public ResponseEntity<List<RegistroAsistenciaResponseDTO>> miHistorial() {
        return ResponseEntity.ok(service.historialEmpleadoActual());
    }

    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<RegistroAsistenciaResponseDTO>> buscarPorEmpleado(@PathVariable Long empleadoId) {
        return ResponseEntity.ok(service.buscarPorEmpleado(empleadoId));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<RegistroAsistenciaResponseDTO>> buscarPorEmpleadoYFecha(
            @RequestParam Long empleadoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(service.buscarPorEmpleadoYFecha(empleadoId, fecha));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<RegistroAsistenciaResponseDTO>> buscarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(service.buscarPorEstado(estado));
    }

    @GetMapping("/hoy")
    public ResponseEntity<List<RegistroAsistenciaResponseDTO>> asistenciasHoy(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        if (fecha != null) {
            return ResponseEntity.ok(service.asistenciasPorFecha(fecha));
        }
        return ResponseEntity.ok(service.asistenciasHoy());
    }

    @GetMapping("/incidencias")
    public ResponseEntity<List<RegistroAsistenciaResponseDTO>> incidencias() {
        return ResponseEntity.ok(service.incidenciasAsistencia());
    }

    @GetMapping("/ranking-tardanzas")
    public ResponseEntity<List<Object[]>> rankingTardanzas() {
        return ResponseEntity.ok(service.rankingTardanzas());
    }

    // ============================================
    // ✅ NUEVOS ENDPOINTS
    // ============================================

    /**
     * ✅ Procesa y marca faltas automáticas para todos los empleados
     * Solo accesible por ADMIN
     */
    @PostMapping("/procesar-faltas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> procesarFaltasAutomaticas() {
        try {
            service.procesarFaltasAutomaticas();
            return ResponseEntity.ok(Map.of(
                "mensaje", "✅ Faltas automáticas procesadas correctamente",
                "fecha", LocalDate.now().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "❌ Error al procesar faltas: " + e.getMessage()
            ));
        }
    }

    /**
     * ✅ Detecta patrón de tardanza para un empleado específico
     */
    @GetMapping("/empleado/{empleadoId}/patron-tardanza")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Map<String, String>> detectarPatronTardanza(@PathVariable Long empleadoId) {
        String patron = service.detectarPatronTardanza(empleadoId);
        return ResponseEntity.ok(Map.of(
            "empleadoId", empleadoId.toString(),
            "patronDetectado", patron != null ? patron : "NINGUNO"
        ));
    }

    /**
     * ✅ Obtiene estadísticas de asistencia de un empleado en un período
     */
    @GetMapping("/empleado/{empleadoId}/estadisticas")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasEmpleado(
            @PathVariable Long empleadoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        
        List<RegistroAsistenciaResponseDTO> registros = service.buscarPorEmpleado(empleadoId);
        
        long totalEntradas = registros.stream()
                .filter(r -> "ENTRADA".equals(r.getTipoMarcacion()))
                .count();
        long totalSalidas = registros.stream()
                .filter(r -> "SALIDA".equals(r.getTipoMarcacion()))
                .count();
        long totalTardanzas = registros.stream()
                .filter(r -> r.getMinutosTardanza() != null && r.getMinutosTardanza() > 0)
                .count();
        long totalFaltas = registros.stream()
                .filter(r -> r.getEsFalta() != null && r.getEsFalta())
                .count();
        
        return ResponseEntity.ok(Map.of(
            "empleadoId", empleadoId,
            "periodo", Map.of("inicio", inicio.toString(), "fin", fin.toString()),
            "totalEntradas", totalEntradas,
            "totalSalidas", totalSalidas,
            "totalTardanzas", totalTardanzas,
            "totalFaltas", totalFaltas
        ));
    }

    /**
     * ✅ Lista todos los registros completos (con relaciones)
     */
    @GetMapping("/completo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RegistroAsistenciaResponseDTO>> listarCompleto() {
        return ResponseEntity.ok(service.listarCompleto());
    }
}