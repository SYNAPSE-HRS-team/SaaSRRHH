package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.FeedbackAnonimoRequestDTO;
import com.SaasRRHH.main.DTO.FeedbackAnonimoResponseDTO;
import com.SaasRRHH.main.model.FeedbackAnonimo;
import com.SaasRRHH.main.services.FeedbackAnonimoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/feedback-anonimo")
public class FeedbackAnonimoController {

    private final FeedbackAnonimoService service;

    @PostMapping
    public ResponseEntity<?> enviar(@RequestBody FeedbackAnonimoRequestDTO request) {
        try {
            FeedbackAnonimoResponseDTO creado = service.enviarFeedback(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<FeedbackAnonimoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<FeedbackAnonimoResponseDTO>> porCategoria(
            @PathVariable FeedbackAnonimo.CategoriaFeedback categoria) {
        return ResponseEntity.ok(service.listarPorCategoria(categoria));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<FeedbackAnonimoResponseDTO>> porEstado(
            @PathVariable FeedbackAnonimo.EstadoFeedback estado) {
        return ResponseEntity.ok(service.listarPorEstado(estado));
    }

    @GetMapping("/rango")
    public ResponseEntity<List<FeedbackAnonimoResponseDTO>> porRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(service.listarPorRangoFechas(inicio, fin));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestParam FeedbackAnonimo.EstadoFeedback estado) {
        try {
            FeedbackAnonimoResponseDTO actualizado = service.cambiarEstado(id, estado);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}
