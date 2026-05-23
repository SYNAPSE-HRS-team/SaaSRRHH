package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.TareaAsignada;
import com.SaasRRHH.main.model.TareaAsignada.EstadoTarea;
import com.SaasRRHH.main.services.TareaAsignadaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<TareaAsignada>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TareaAsignada> obtener(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TareaAsignada> crear(@RequestBody TareaAsignada tarea) {
        try {
            TareaAsignada nuevaTarea = service.guardar(tarea);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaTarea);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TareaAsignada> actualizar(@PathVariable Long id, @RequestBody TareaAsignada tarea) {
        return service.actualizar(id, tarea)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
    public ResponseEntity<List<TareaAsignada>> buscarPorEmpleado(@PathVariable Long empleadoId) {
        return ResponseEntity.ok(service.buscarPorEmpleado(empleadoId));
    }

    @GetMapping("/supervisor/{supervisorId}")
    public ResponseEntity<List<TareaAsignada>> buscarPorSupervisor(@PathVariable Long supervisorId) {
        return ResponseEntity.ok(service.buscarPorSupervisor(supervisorId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<TareaAsignada>> buscarPorEstado(@PathVariable String estado) {
        try {
            EstadoTarea estadoEnum = EstadoTarea.valueOf(estado.toUpperCase());
            return ResponseEntity.ok(service.buscarPorEstado(estadoEnum));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/empleado/{empleadoId}/fecha")
    public ResponseEntity<List<TareaAsignada>> buscarPorEmpleadoYFecha(
            @PathVariable Long empleadoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(service.buscarPorEmpleadoYFecha(empleadoId, fecha));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<TareaAsignada> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        try {
            EstadoTarea nuevoEstado = EstadoTarea.valueOf(estado.toUpperCase());
            TareaAsignada tareaActualizada = service.cambiarEstado(id, nuevoEstado);
            return ResponseEntity.ok(tareaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}




