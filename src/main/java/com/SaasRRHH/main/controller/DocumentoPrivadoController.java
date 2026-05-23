package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.DocumentoPrivado;
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

    private final DocumentoPrivadoService documentoPrivadoService;

    @GetMapping
    public ResponseEntity<List<DocumentoPrivado>> listar() {

        return ResponseEntity.ok(documentoPrivadoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentoPrivado> buscarPorId(@PathVariable Long id) {

        return documentoPrivadoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DocumentoPrivado> guardar(
            @Valid @RequestBody DocumentoPrivado documentoPrivado) {

        DocumentoPrivado nuevoDocumento =
                documentoPrivadoService.guardar(documentoPrivado);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nuevoDocumento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentoPrivado> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody DocumentoPrivado documentoPrivado) {

        try {

            DocumentoPrivado documentoActualizado =
                    documentoPrivadoService.actualizar(id, documentoPrivado);

            return ResponseEntity.ok(documentoActualizado);

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        try {

            documentoPrivadoService.eliminar(id);

            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }
}