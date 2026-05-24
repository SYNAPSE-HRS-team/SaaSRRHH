package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.DocumentoPrivadoRequestDTO;
import com.SaasRRHH.main.DTO.DocumentoPrivadoResponseDTO;
import com.SaasRRHH.main.services.DocumentoPrivadoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documentos-privados")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DocumentoPrivadoController {

    private final DocumentoPrivadoService service;

    // LISTAR
    @GetMapping
    public ResponseEntity<List<DocumentoPrivadoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<DocumentoPrivadoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // CREAR
    @PostMapping
    public ResponseEntity<DocumentoPrivadoResponseDTO> guardar(
            @Valid @RequestBody DocumentoPrivadoRequestDTO dto) {

        DocumentoPrivadoResponseDTO response = service.guardar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<DocumentoPrivadoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody DocumentoPrivadoRequestDTO dto) {

        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    // ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}