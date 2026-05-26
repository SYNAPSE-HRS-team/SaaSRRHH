package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.EncuestaBienestarRequestDTO;
import com.SaasRRHH.main.DTO.EncuestaBienestarResponseDTO;
import com.SaasRRHH.main.DTO.ResumenBienestarDTO;
import com.SaasRRHH.main.services.EncuestaBienestarService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/encuestas-bienestar")
public class EncuestaBienestarController {

    private final EncuestaBienestarService service;

    @GetMapping
    public ResponseEntity<List<EncuestaBienestarResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        try {
            EncuestaBienestarResponseDTO dto = service.obtenerPorId(id);
            if (dto == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No encontrado");
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody EncuestaBienestarRequestDTO encuesta) {
        try {
            EncuestaBienestarResponseDTO creado = service.guardar(encuesta);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody EncuestaBienestarRequestDTO encuesta) {
        try {
            EncuestaBienestarResponseDTO actualizado = service.actualizar(id, encuesta);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<EncuestaBienestarResponseDTO>> historialEmpleado(@PathVariable Long empleadoId) {
        return ResponseEntity.ok(service.obtenerHistorialEmpleado(empleadoId));
    }

    @GetMapping("/rango")
    public ResponseEntity<List<EncuestaBienestarResponseDTO>> porRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(service.obtenerPorRangoFechas(inicio, fin));
    }

    @GetMapping("/riesgo")
    public ResponseEntity<List<Long>> empleadosEnRiesgo() {
        return ResponseEntity.ok(service.obtenerEmpleadosEnRiesgo());
    }

    @GetMapping("/resumen")
    public ResponseEntity<ResumenBienestarDTO> resumen(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(service.obtenerResumenMensual(inicio, fin));
    }
}