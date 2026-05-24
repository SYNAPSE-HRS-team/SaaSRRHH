package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.TipoDocumentoRequestDTO;
import com.SaasRRHH.main.DTO.TipoDocumentoResponseDTO;
import com.SaasRRHH.main.services.TipoDocumentoService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-documento")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TipoDocumentoController {

    private final TipoDocumentoService service;

    // LISTAR
    @GetMapping
    public ResponseEntity<List<TipoDocumentoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<TipoDocumentoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // CREAR
    @PostMapping
    public ResponseEntity<TipoDocumentoResponseDTO> crear(
            @RequestBody TipoDocumentoRequestDTO dto) {

        TipoDocumentoResponseDTO response = service.guardar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<TipoDocumentoResponseDTO> actualizar(
            @PathVariable Long id,
            @RequestBody TipoDocumentoRequestDTO dto) {

        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    // ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}