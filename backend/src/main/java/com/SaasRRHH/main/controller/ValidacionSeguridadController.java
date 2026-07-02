package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.ValidacionSeguridadRequestDTO;
import com.SaasRRHH.main.DTO.ValidacionSeguridadResponseDTO;
import com.SaasRRHH.main.services.ValidacionSeguridadService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/validaciones-seguridad")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ValidacionSeguridadController {

    private final ValidacionSeguridadService service;

    // =========================================
    // CRUD
    // =========================================

    @GetMapping
    public ResponseEntity<List<ValidacionSeguridadResponseDTO>>
    listar() {

        return ResponseEntity.ok(
                service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ValidacionSeguridadResponseDTO>
    buscarPorId(@PathVariable Long id) {

        return ResponseEntity.ok(
                service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ValidacionSeguridadResponseDTO>
    guardar(
            @Valid
            @RequestBody
            ValidacionSeguridadRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ValidacionSeguridadResponseDTO>
    actualizar(
            @PathVariable Long id,
            @Valid
            @RequestBody
            ValidacionSeguridadRequestDTO dto) {

        return ResponseEntity.ok(
                service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    eliminar(@PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    // =========================================
    // CONSULTAS
    // =========================================

    @GetMapping("/totp/{valido}")
    public ResponseEntity<List<ValidacionSeguridadResponseDTO>>
    buscarPorTotp(
            @PathVariable Boolean valido) {

        return ResponseEntity.ok(
                service.buscarPorTotpValido(valido));
    }

    @GetMapping("/recientes")
    public ResponseEntity<List<ValidacionSeguridadResponseDTO>>
    recientes() {

        return ResponseEntity.ok(
                service.recientes());
    }

    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<ValidacionSeguridadResponseDTO>>
    buscarPorEmpleado(
            @PathVariable Long empleadoId) {

        return ResponseEntity.ok(
                service.buscarPorEmpleado(
                        empleadoId));
    }

    @GetMapping("/fallidos")
    public ResponseEntity<List<ValidacionSeguridadResponseDTO>>
    intentosFallidos() {

        return ResponseEntity.ok(
                service.intentosFallidos());
    }
}