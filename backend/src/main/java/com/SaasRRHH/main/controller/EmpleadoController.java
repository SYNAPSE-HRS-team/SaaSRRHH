package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.EmpleadoRequestDTO;
import com.SaasRRHH.main.DTO.EmpleadoResponseDTO;
import com.SaasRRHH.main.services.EmpleadoService;
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
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    @GetMapping
    public ResponseEntity<List<EmpleadoResponseDTO>> listar() {
        return ResponseEntity.ok(empleadoService.listar());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<EmpleadoResponseDTO>> listarActivos() {
        return ResponseEntity.ok(empleadoService.listarActivos());
    }

    // ✅ ESTE ES EL ENDPOINT QUE FALTABA
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<EmpleadoResponseDTO> buscarPorUsuarioId(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(empleadoService.buscarPorUsuarioId(usuarioId));
    }

    @GetMapping("/supervisores")
    public ResponseEntity<List<EmpleadoResponseDTO>> listarSupervisores() {
        return ResponseEntity.ok(empleadoService.listarSupervisores());
    }

    @GetMapping("/trabajadores")
    public ResponseEntity<List<EmpleadoResponseDTO>> listarTrabajadores() {
        return ResponseEntity.ok(empleadoService.listarTrabajadores());
    }

    @GetMapping("/trabajadores-rol")
    public ResponseEntity<List<EmpleadoResponseDTO>> listarTrabajadoresByRol() {
        return ResponseEntity.ok(empleadoService.listarTrabajadoresByRol());
    }

    @GetMapping("/supervisores-rol")
    public ResponseEntity<List<EmpleadoResponseDTO>> listarSupervisoresByRol() {
        return ResponseEntity.ok(empleadoService.listarSupervisoresByRol());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(empleadoService.buscarPorId(id));
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<EmpleadoResponseDTO> buscarPorDni(@PathVariable String dni) {
        return ResponseEntity.ok(empleadoService.buscarPorDni(dni));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmpleadoResponseDTO> guardar(@RequestBody EmpleadoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoService.guardar(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmpleadoResponseDTO> actualizar(@PathVariable Long id, @RequestBody EmpleadoRequestDTO dto) {
        return ResponseEntity.ok(empleadoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        empleadoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/resumen-puntualidad")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<EmpleadoResponseDTO> obtenerResumenPuntualidad(@PathVariable Long id) {
        return ResponseEntity.ok(empleadoService.obtenerResumenPuntualidad(id));
    }

    @GetMapping("/{id}/horas-contrato")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> calcularHorasContrato(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        long horasContrato = empleadoService.calcularHorasContrato(id, inicio, fin);
        long horasReales = empleadoService.calcularHorasReales(id, inicio, fin);
        return ResponseEntity.ok(Map.of(
                "empleadoId", id,
                "periodo", Map.of("inicio", inicio.toString(), "fin", fin.toString()),
                "horasContrato", horasContrato,
                "horasReales", horasReales,
                "diferencia", horasReales - horasContrato));
    }

    @PatchMapping("/{id}/horario")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmpleadoResponseDTO> actualizarHorario(@PathVariable Long id, @RequestBody Map<String, Object> horario) {
        EmpleadoRequestDTO dto = new EmpleadoRequestDTO();
        if (horario.containsKey("horaEntrada")) dto.setHoraEntrada(java.time.LocalTime.parse((String) horario.get("horaEntrada")));
        if (horario.containsKey("horaSalida")) dto.setHoraSalida(java.time.LocalTime.parse((String) horario.get("horaSalida")));
        if (horario.containsKey("diasLaborables")) dto.setDiasLaborables((String) horario.get("diasLaborables"));
        if (horario.containsKey("toleranciaMinutos")) dto.setToleranciaMinutos((Integer) horario.get("toleranciaMinutos"));
        return ResponseEntity.ok(empleadoService.actualizar(id, dto));
    }

    @PatchMapping("/{id}/tipo-pago")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmpleadoResponseDTO> actualizarTipoPago(@PathVariable Long id, @RequestBody Map<String, Object> tipoPago) {
        EmpleadoRequestDTO dto = new EmpleadoRequestDTO();
        if (tipoPago.containsKey("tipoPago")) dto.setTipoPago((String) tipoPago.get("tipoPago"));
        if (tipoPago.containsKey("montoPago")) dto.setMontoPago(new java.math.BigDecimal(tipoPago.get("montoPago").toString()));
        return ResponseEntity.ok(empleadoService.actualizar(id, dto));
    }
}