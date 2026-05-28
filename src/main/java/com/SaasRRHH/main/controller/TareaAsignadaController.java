package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.TareaAsignadaRequestDTO;
import com.SaasRRHH.main.DTO.TareaAsignadaResponseDTO;
import com.SaasRRHH.main.model.TareaAsignada.EstadoTarea;
import com.SaasRRHH.main.services.TareaAsignadaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tareas-asignadas")
@CrossOrigin(origins = "*")
public class TareaAsignadaController {

    private final TareaAsignadaService service;

    public TareaAsignadaController(TareaAsignadaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TareaAsignadaResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TareaAsignadaResponseDTO> obtener(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<TareaAsignadaResponseDTO> crear(@RequestBody TareaAsignadaRequestDTO tarea) {
        try {
            TareaAsignadaResponseDTO nuevaTarea = service.guardar(tarea);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaTarea);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TareaAsignadaResponseDTO> actualizar(@PathVariable Long id,
            @RequestBody TareaAsignadaRequestDTO tarea) {
        try {
            TareaAsignadaResponseDTO actualizado = service.actualizar(id, tarea);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<TareaAsignadaResponseDTO>> buscarPorEmpleado(@PathVariable Long empleadoId) {
        return ResponseEntity.ok(service.buscarPorEmpleado(empleadoId));
    }

    @GetMapping("/supervisor/{supervisorId}")
    public ResponseEntity<List<TareaAsignadaResponseDTO>> buscarPorSupervisor(@PathVariable Long supervisorId) {
        return ResponseEntity.ok(service.buscarPorSupervisor(supervisorId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<TareaAsignadaResponseDTO>> buscarPorEstado(@PathVariable String estado) {
        try {
            EstadoTarea estadoEnum = EstadoTarea.valueOf(estado.toUpperCase());
            return ResponseEntity.ok(service.buscarPorEstado(estadoEnum));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/empleado/{empleadoId}/fecha")
    public ResponseEntity<List<TareaAsignadaResponseDTO>> buscarPorEmpleadoYFecha(
            @PathVariable Long empleadoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(service.buscarPorEmpleadoYFecha(empleadoId, fecha));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<TareaAsignadaResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        try {
            EstadoTarea nuevoEstado = EstadoTarea.valueOf(estado.toUpperCase());
            TareaAsignadaResponseDTO tareaActualizada = service.cambiarEstado(id, nuevoEstado);
            return ResponseEntity.ok(tareaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // En TareaAsignadaController.java
    @GetMapping("/seguimiento/{areaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<TareaAsignadaResponseDTO>> obtenerSeguimientoArea(
            @PathVariable Long areaId) {
        // Lógica para listar tareas en progreso en un área específica
        return ResponseEntity.ok(service.buscarPorAreaYEstado(areaId, EstadoTarea.EN_PROGRESO));
    }
}
