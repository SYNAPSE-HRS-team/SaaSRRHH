package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.TipoDocumento;
import com.SaasRRHH.main.services.TipoDocumentoService;

import jakarta.validation.Valid;

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

    private final TipoDocumentoService tipoDocumentoService;

    @GetMapping
    public ResponseEntity<List<TipoDocumento>> listar() {

        return ResponseEntity.ok(tipoDocumentoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoDocumento> buscarPorId(@PathVariable Long id) {

        return tipoDocumentoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TipoDocumento> guardar(
            @Valid @RequestBody TipoDocumento tipoDocumento) {

        TipoDocumento nuevoTipoDocumento =
                tipoDocumentoService.guardar(tipoDocumento);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nuevoTipoDocumento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoDocumento> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody TipoDocumento tipoDocumento) {

        try {

            TipoDocumento tipoDocumentoActualizado =
                    tipoDocumentoService.actualizar(id, tipoDocumento);

            return ResponseEntity.ok(tipoDocumentoActualizado);

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        try {

            tipoDocumentoService.eliminar(id);

            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }
}