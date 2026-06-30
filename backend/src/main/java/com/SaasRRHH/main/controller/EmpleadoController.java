package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.EmpleadoRequestDTO;
import com.SaasRRHH.main.DTO.EmpleadoResponseDTO;
import com.SaasRRHH.main.services.EmpleadoService;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmpleadoController {

        private final EmpleadoService service;

        // ===================================
        // CRUD
        // ===================================

        @GetMapping
        public ResponseEntity<List<EmpleadoResponseDTO>> listar() {
                return ResponseEntity.ok(service.listar());
        }

        @GetMapping("/supervisores")
        public ResponseEntity<List<EmpleadoResponseDTO>> listarSupervisores() {
                return ResponseEntity.ok(service.listarSupervisores());
        }

        @GetMapping("/{id}")
        public ResponseEntity<EmpleadoResponseDTO> obtener(@PathVariable Long id) {
                try {
                        return ResponseEntity.ok(service.buscarPorId(id));
                } catch (RuntimeException e) {
                        return ResponseEntity.notFound().build();
                }
        }

        @GetMapping("/dni/{dni}")
        public ResponseEntity<EmpleadoResponseDTO> buscarPorDni(@PathVariable String dni) {
                try {
                        return ResponseEntity.ok(service.buscarPorDni(dni));
                } catch (RuntimeException e) {
                        return ResponseEntity.notFound().build();
                }
        }

        @GetMapping("/activos")
        public ResponseEntity<List<EmpleadoResponseDTO>> listarActivos() {
                return ResponseEntity.ok(service.listarActivos());
        }

        @GetMapping("/trabajadores")
        public ResponseEntity<List<EmpleadoResponseDTO>> listarTrabajadores() {
                return ResponseEntity.ok(service.listarTrabajadores());
        }

        // ✅ TU MÉTODO (Nancy)
        @GetMapping("/usuario/{usuarioId}")
        public ResponseEntity<EmpleadoResponseDTO> buscarPorUsuarioId(@PathVariable Long usuarioId) {
                try {
                        return ResponseEntity.ok(service.buscarPorUsuarioId(usuarioId));
                } catch (RuntimeException e) {
                        return ResponseEntity.notFound().build();
                }
        }

        // ✅ MÉTODOS DE MIGUEL
        @GetMapping("/trabajadores-rol")
        public ResponseEntity<List<EmpleadoResponseDTO>> listarTrabajadoresByRol() {
                return ResponseEntity.ok(service.listarTrabajadoresByRol());
        }

        @GetMapping("/supervisores-rol")
        public ResponseEntity<List<EmpleadoResponseDTO>> listarSupervisoresByRol() {
                return ResponseEntity.ok(service.listarSupervisoresByRol());
        }

        @PostMapping
        public ResponseEntity<EmpleadoResponseDTO> crear(@RequestBody EmpleadoRequestDTO dto) {
                try {
                        EmpleadoResponseDTO empleado = service.guardar(dto);
                        return ResponseEntity.status(HttpStatus.CREATED).body(empleado);
                } catch (RuntimeException e) {
                        return ResponseEntity.badRequest().build();
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

        // ===================================
        // CONSULTAS JPQL
        // ===================================

        @GetMapping("/cargo/{cargo}")
        public ResponseEntity<List<EmpleadoResponseDTO>> buscarPorCargo(@PathVariable String cargo) {
                return ResponseEntity.ok(service.buscarPorCargo(cargo));
        }

        @GetMapping("/cargo-activo")
        public ResponseEntity<List<EmpleadoResponseDTO>> buscarPorCargoYActivo(
                        @RequestParam String cargo,
                        @RequestParam Boolean activo) {
                return ResponseEntity.ok(service.buscarPorCargoYActivo(cargo, activo));
        }

        @GetMapping("/activos-usuario")
        public ResponseEntity<List<EmpleadoResponseDTO>> listarActivosConUsuario() {
                return ResponseEntity.ok(service.listarActivosConUsuario());
        }

        @GetMapping("/contratos-vencidos")
        public ResponseEntity<List<EmpleadoResponseDTO>> contratosVencidos() {
                return ResponseEntity.ok(service.contratosVencidos());
        }

        @GetMapping("/contratos-por-vencer")
        public ResponseEntity<List<EmpleadoResponseDTO>> contratosPorVencer(
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaLimite) {
                return ResponseEntity.ok(service.contratosPorVencer(fechaLimite));
        }

        @GetMapping("/estadisticas/cargos")
        public ResponseEntity<List<Object[]>> contarEmpleadosPorCargo() {
                return ResponseEntity.ok(service.contarEmpleadosPorCargo());
        }
}