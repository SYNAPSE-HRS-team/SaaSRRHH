package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.RegistroAsistencia;
import com.SaasRRHH.main.services.RegistroAsistenciaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registros-asistencia")
@CrossOrigin(origins = "*")
public class RegistroAsistenciaController {

    private final RegistroAsistenciaService service;

    public RegistroAsistenciaController(RegistroAsistenciaService service) {
        this.service = service;
    }

    // Listar todos
    @GetMapping
    public ResponseEntity<List<RegistroAsistencia>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<RegistroAsistencia> obtener(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear Registro
    @PostMapping
    public ResponseEntity<RegistroAsistencia> crear(@RequestBody RegistroAsistencia registroAsistencia) {
        try {
            RegistroAsistencia nuevoRegistro = service.guardar(registroAsistencia);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRegistro);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Registrar entrada (endpoint específico)
    @PostMapping("/entrada")
    public ResponseEntity<RegistroAsistencia> registrarEntrada(@RequestBody Map<String, Object> payload) {
        try {
            Long empleadoId = Long.valueOf(payload.get("empleadoId").toString());
            String metodo = payload.containsKey("metodo") ? payload.get("metodo").toString() : "QR";
            RegistroAsistencia registro = service.registrarEntrada(empleadoId, metodo);
            return ResponseEntity.status(HttpStatus.CREATED).body(registro);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Registrar salida (endpoint específico)
    @PostMapping("/salida")
    public ResponseEntity<RegistroAsistencia> registrarSalida(@RequestBody Map<String, Object> payload) {
        try {
            Long empleadoId = Long.valueOf(payload.get("empleadoId").toString());
            String metodo = payload.containsKey("metodo") ? payload.get("metodo").toString() : "QR";
            RegistroAsistencia registro = service.registrarSalida(empleadoId, metodo);
            return ResponseEntity.status(HttpStatus.CREATED).body(registro);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Buscar por empleado
    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<RegistroAsistencia>> buscarPorEmpleado(@PathVariable Long empleadoId) {
        List<RegistroAsistencia> registros = service.buscarPorEmpleado(empleadoId);
        return ResponseEntity.ok(registros);
    }

    // Buscar por empleado y fecha
    @GetMapping("/empleado/{empleadoId}/fecha")
    public ResponseEntity<List<RegistroAsistencia>> buscarPorEmpleadoYFecha(
            @PathVariable Long empleadoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<RegistroAsistencia> registros = service.buscarPorEmpleadoYFecha(empleadoId, fecha);
        return ResponseEntity.ok(registros);
    }

    // Buscar por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<RegistroAsistencia>> buscarPorEstado(@PathVariable String estado) {
        List<RegistroAsistencia> registros = service.buscarPorEstado(estado);
        return ResponseEntity.ok(registros);
    }

    // Actualizar registro
    @PutMapping("/{id}")
    public ResponseEntity<RegistroAsistencia> actualizar(@PathVariable Long id, @RequestBody RegistroAsistencia registroAsistencia) {
        return service.buscarPorId(id)
                .map(existing -> {
                    registroAsistencia.setId(id);
                    RegistroAsistencia actualizado = service.guardar(registroAsistencia);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar registro
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}