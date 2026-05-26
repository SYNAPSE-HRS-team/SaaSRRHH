package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.DispositivoAutorizadoRequestDTO;
import com.SaasRRHH.main.DTO.DispositivoAutorizadoResponseDTO;
import com.SaasRRHH.main.services.DispositivoAutorizadoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dispositivos-autorizados")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DispositivoAutorizadoController {

    private final DispositivoAutorizadoService service;

    // ===================================
    // CRUD
    // ===================================

    @GetMapping
    public ResponseEntity<List<DispositivoAutorizadoResponseDTO>>
    listarTodos() {

        return ResponseEntity.ok(
                service.listarTodo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DispositivoAutorizadoResponseDTO>
    buscarPorId(@PathVariable Long id) {

        return ResponseEntity.ok(
                service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<DispositivoAutorizadoResponseDTO>
    guardar(
            @Valid
            @RequestBody
            DispositivoAutorizadoRequestDTO dto) {

        DispositivoAutorizadoResponseDTO response =
                service.guardar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DispositivoAutorizadoResponseDTO>
    actualizar(
            @PathVariable Long id,
            @Valid
            @RequestBody
            DispositivoAutorizadoRequestDTO dto) {

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

    // ===================================
    // CONSULTAS
    // ===================================

    @GetMapping("/activos")
    public ResponseEntity<
            List<DispositivoAutorizadoResponseDTO>>
    listarActivos() {

        return ResponseEntity.ok(
                service.listarActivos());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<
            List<DispositivoAutorizadoResponseDTO>>
    buscarPorUsuario(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                service.buscarPorUsuario(usuarioId));
    }

    @GetMapping("/existe")
    public ResponseEntity<Boolean>
    existeHardwareRegistrado(
            @RequestParam Long usuarioId,
            @RequestParam String hardwareId) {

        return ResponseEntity.ok(
                service.existeHardwareRegistrado(
                        usuarioId,
                        hardwareId));
    }

    @GetMapping("/recientes")
    public ResponseEntity<
            List<DispositivoAutorizadoResponseDTO>>
    dispositivosRecientes() {

        return ResponseEntity.ok(
                service.dispositivosRecientes());
    }
}