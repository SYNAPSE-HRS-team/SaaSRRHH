package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.RegistroAsistenciaRequestDTO;
import com.SaasRRHH.main.DTO.RegistroAsistenciaResponseDTO;
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
    public ResponseEntity<List<RegistroAsistenciaResponseDTO>> listar() {
        return (ResponseEntity<List<RegistroAsistenciaResponseDTO>>) ResponseEntity.ok(service.listar());
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<RegistroAsistenciaResponseDTO> obtener(@PathVariable Long id) {
        try {
            RegistroAsistenciaResponseDTO dto = service.buscarPorId(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Crear Registro
    @PostMapping
    public ResponseEntity<RegistroAsistenciaResponseDTO> crear(@RequestBody RegistroAsistenciaRequestDTO registroAsistencia) {
        try {
            RegistroAsistenciaResponseDTO nuevoRegistro = service.guardar(registroAsistencia);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRegistro);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Registrar entrada (endpoint específico)
    @PostMapping("/entrada")
    public ResponseEntity<RegistroAsistenciaResponseDTO> registrarEntrada(@RequestBody Map<String, Object> payload) {
        try {
            Long empleadoId = Long.valueOf(payload.get("empleadoId").toString());
            String metodo = payload.containsKey("metodo") ? payload.get("metodo").toString() : "QR";
            RegistroAsistenciaResponseDTO registro = service.registrarEntrada(empleadoId, metodo);
            return ResponseEntity.status(HttpStatus.CREATED).body(registro);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Registrar salida (endpoint específico)
    @PostMapping("/salida")
    public ResponseEntity<RegistroAsistenciaResponseDTO> registrarSalida(@RequestBody Map<String, Object> payload) {
        try {
            Long empleadoId = Long.valueOf(payload.get("empleadoId").toString());
            String metodo = payload.containsKey("metodo") ? payload.get("metodo").toString() : "QR";
            RegistroAsistenciaResponseDTO registro = service.registrarSalida(empleadoId, metodo);
            return ResponseEntity.status(HttpStatus.CREATED).body(registro);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Buscar por empleado
    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<RegistroAsistenciaResponseDTO>> buscarPorEmpleado(@PathVariable Long empleadoId) {
        List<RegistroAsistenciaResponseDTO> registros = service.buscarPorEmpleado(empleadoId);
        return ResponseEntity.ok(registros);
    }

    // Buscar por empleado y fecha
    @GetMapping("/empleado/{empleadoId}/fecha")
    public ResponseEntity<List<RegistroAsistenciaResponseDTO>> buscarPorEmpleadoYFecha(
            @PathVariable Long empleadoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<RegistroAsistenciaResponseDTO> registros = service.buscarPorEmpleadoYFecha(empleadoId, fecha);
        return ResponseEntity.ok(registros);
    }

    // Buscar por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<RegistroAsistenciaResponseDTO>> buscarPorEstado(@PathVariable String estado) {
        List<RegistroAsistenciaResponseDTO> registros = service.buscarPorEstado(estado);
        return ResponseEntity.ok(registros);
    }

    // Actualizar registro
    @PutMapping("/{id}")
    public ResponseEntity<RegistroAsistenciaResponseDTO> actualizar(@PathVariable Long id, @RequestBody RegistroAsistenciaRequestDTO registroAsistencia) {
        try {
            // verificar existencia
            service.buscarPorId(id);
            registroAsistencia.setId(id);
            RegistroAsistenciaResponseDTO actualizado = service.guardar(registroAsistencia);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
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