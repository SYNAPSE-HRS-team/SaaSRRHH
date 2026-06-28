package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.AsistenciaCalendarioAnualDTO;
import com.SaasRRHH.main.DTO.AsistenciaCalendarioMesDTO;
import com.SaasRRHH.main.DTO.AsistenciaQrDTO;
import com.SaasRRHH.main.DTO.AsistenciaScanRequestDTO;
import com.SaasRRHH.main.DTO.RegistroAsistenciaRequestDTO;
import com.SaasRRHH.main.DTO.RegistroAsistenciaResponseDTO;
import com.SaasRRHH.main.services.RegistroAsistenciaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RegistroAsistenciaController {

    private final RegistroAsistenciaService service;

    // ===================================
    // CRUD
    // ===================================

    @GetMapping
    public ResponseEntity<List<RegistroAsistenciaResponseDTO>> listar() {

        return (ResponseEntity<List<RegistroAsistenciaResponseDTO>>) ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroAsistenciaResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<RegistroAsistenciaResponseDTO> guardar(
            @Valid @RequestBody RegistroAsistenciaRequestDTO dto) {

        RegistroAsistenciaResponseDTO response =
                service.guardar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.noContent().build();
    }

    // ===================================
    // REGISTRO ENTRADA / SALIDA
    // ===================================

    @PostMapping("/entrada/{empleadoId}")
    public ResponseEntity<RegistroAsistenciaResponseDTO>
    registrarEntrada(
            @PathVariable Long empleadoId,
            @RequestParam(required = false)
            String metodo) {

        return ResponseEntity.ok(
                service.registrarEntrada(
                        empleadoId,
                        metodo));
    }

    @PostMapping("/salida/{empleadoId}")
    public ResponseEntity<RegistroAsistenciaResponseDTO>
    registrarSalida(
            @PathVariable Long empleadoId,
            @RequestParam(required = false)
            String metodo) {

        return ResponseEntity.ok(
                service.registrarSalida(
                        empleadoId,
                        metodo));
    }

    // ===================================
    // CONSULTAS
    // ===================================

    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<RegistroAsistenciaResponseDTO>>
    buscarPorEmpleado(
            @PathVariable Long empleadoId) {

        return ResponseEntity.ok(
                service.buscarPorEmpleado(
                        empleadoId));
    }

    @GetMapping("/empleado/{empleadoId}/fecha")
    public ResponseEntity<List<RegistroAsistenciaResponseDTO>>
    buscarPorEmpleadoYFecha(
            @PathVariable Long empleadoId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha) {

        return ResponseEntity.ok(
                service.buscarPorEmpleadoYFecha(
                        empleadoId,
                        fecha));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<RegistroAsistenciaResponseDTO>>
    buscarPorEstado(
            @PathVariable String estado) {

        return ResponseEntity.ok(
                service.buscarPorEstado(
                        estado));
    }

    // ===================================
    // CONSULTAS ANALITICAS
    // ===================================

    @GetMapping("/hoy")
    public ResponseEntity<List<RegistroAsistenciaResponseDTO>>
    asistenciasHoy() {

        return ResponseEntity.ok(
                service.asistenciasHoy());
    }

    @GetMapping("/incidencias")
    public ResponseEntity<List<RegistroAsistenciaResponseDTO>>
    incidenciasAsistencia() {

        return ResponseEntity.ok(
                service.incidenciasAsistencia());
    }

    @GetMapping("/mensual/{empleadoId}")
    public ResponseEntity<Long>
    contarAsistenciasMensuales(

            @PathVariable Long empleadoId,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime inicio,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fin) {

        return ResponseEntity.ok(
                service.contarAsistenciasMensuales(
                        empleadoId,
                        inicio,
                        fin));
    }

    @GetMapping("/ranking-tardanzas")
    public ResponseEntity<List<Object[]>>
    rankingTardanzas() {

        return ResponseEntity.ok(
                service.rankingTardanzas());
    }

    @GetMapping("/ya-marco")
    public ResponseEntity<Boolean>
    yaMarcoHoy(

            @RequestParam Long empleadoId,

            @RequestParam String tipo) {

        return ResponseEntity.ok(
                service.yaMarcoHoy(
                        empleadoId,
                        tipo));
    }

    @GetMapping("/completo")
    public ResponseEntity<List<RegistroAsistenciaResponseDTO>>
    listarCompleto() {

        return ResponseEntity.ok(
                service.listarCompleto());
    }

    // ===================================
    // QR Y CALENDARIO DE ASISTENCIA
    // ===================================

    @GetMapping("/mi-qr")
    public ResponseEntity<AsistenciaQrDTO> miQr() {
        return ResponseEntity.ok(service.generarQrEmpleadoActual());
    }

    @PostMapping("/scan-qr")
    public ResponseEntity<RegistroAsistenciaResponseDTO> scanQr(
            @RequestBody AsistenciaScanRequestDTO request) {
        return ResponseEntity.ok(service.registrarPorQr(request.getPayload()));
    }

    @GetMapping("/mi-calendario")
    public ResponseEntity<AsistenciaCalendarioMesDTO> miCalendario(
            @RequestParam Integer anio,
            @RequestParam Integer mes) {
        return ResponseEntity.ok(service.calendarioEmpleadoActual(anio, mes));
    }

    @GetMapping("/mi-calendario/anual")
    public ResponseEntity<AsistenciaCalendarioAnualDTO> miCalendarioAnual(
            @RequestParam Integer anio) {
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
}