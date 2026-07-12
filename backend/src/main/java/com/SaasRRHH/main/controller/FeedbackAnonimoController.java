package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.FeedbackAnonimoRequestDTO;
import com.SaasRRHH.main.DTO.FeedbackAnonimoResponseDTO;
import com.SaasRRHH.main.model.FeedbackAnonimo;
import com.SaasRRHH.main.services.FeedbackAnonimoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/feedback-anonimo")
public class FeedbackAnonimoController {

    private final FeedbackAnonimoService service;

    // ============================================
    // ENDPOINTS ORIGINALES
    // ============================================

    @PostMapping
    public ResponseEntity<?> enviar(@RequestBody FeedbackAnonimoRequestDTO request) {
        try {
            FeedbackAnonimoResponseDTO creado = service.enviarFeedback(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FeedbackAnonimoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/categoria/{categoria}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FeedbackAnonimoResponseDTO>> porCategoria(
            @PathVariable FeedbackAnonimo.CategoriaFeedback categoria) {
        return ResponseEntity.ok(service.listarPorCategoria(categoria));
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FeedbackAnonimoResponseDTO>> porEstado(
            @PathVariable FeedbackAnonimo.EstadoFeedback estado) {
        return ResponseEntity.ok(service.listarPorEstado(estado));
    }

    @GetMapping("/rango")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FeedbackAnonimoResponseDTO>> porRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(service.listarPorRangoFechas(inicio, fin));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestParam FeedbackAnonimo.EstadoFeedback estado) {
        try {
            FeedbackAnonimoResponseDTO actualizado = service.cambiarEstado(id, estado);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
    }

    // ============================================
    // ✅ NUEVOS ENDPOINTS
    // ============================================

    /**
     * ✅ Admin responde un feedback y cambia su estado
     */
    @PostMapping("/{id}/responder")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> responderFeedback(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            String respuesta = body.get("respuesta");
            String estadoStr = body.get("estado");
            
            if (respuesta == null || respuesta.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "La respuesta es requerida"));
            }
            
            FeedbackAnonimo.EstadoFeedback estado = FeedbackAnonimo.EstadoFeedback.REVISADO;
            if (estadoStr != null) {
                try {
                    estado = FeedbackAnonimo.EstadoFeedback.valueOf(estadoStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "error", "Estado inválido. Use: REVISADO, NO_PROCEDE o ACEPTADO"
                    ));
                }
            }
            
            FeedbackAnonimoResponseDTO respondido = service.responderFeedback(id, respuesta, estado);
            return ResponseEntity.ok(respondido);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * ✅ Listar feedback de un empleado específico (admin ve todo, empleado ve lo suyo)
     */
    @GetMapping("/empleado/{empleadoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FeedbackAnonimoResponseDTO>> porEmpleado(@PathVariable Long empleadoId) {
        return ResponseEntity.ok(service.listarPorEmpleado(empleadoId));
    }

    /**
     * ✅ Empleado ve sus propios feedbacks
     */
    @GetMapping("/mis-feedbacks")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'TRABAJADOR', 'SUPERVISOR')")
    public ResponseEntity<?> misFeedbacks(@RequestParam Long empleadoId) {
        try {
            List<FeedbackAnonimoResponseDTO> feedbacks = service.listarMisFeedbacks(empleadoId);
            return ResponseEntity.ok(feedbacks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * ✅ Contar feedback pendientes (para badge en dashboard)
     */
    @GetMapping("/pendientes/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> contarPendientes() {
        long count = service.contarPendientes();
        return ResponseEntity.ok(Map.of("pendientes", count));
    }

    /**
     * ✅ Listar solo feedback pendientes
     */
    @GetMapping("/pendientes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FeedbackAnonimoResponseDTO>> listarPendientes() {
        return ResponseEntity.ok(service.listarPorEstado(FeedbackAnonimo.EstadoFeedback.PENDIENTE));
    }

    /**
     * ✅ Listar feedback respondidos
     */
    @GetMapping("/respondidos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FeedbackAnonimoResponseDTO>> listarRespondidos() {
        List<FeedbackAnonimoResponseDTO> todos = service.listar();
        List<FeedbackAnonimoResponseDTO> respondidos = todos.stream()
                .filter(f -> f.getRespuesta() != null && !f.getRespuesta().isBlank())
                .toList();
        return ResponseEntity.ok(respondidos);
    }
}