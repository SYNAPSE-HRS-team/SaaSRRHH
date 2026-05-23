package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.Familiar;
import com.SaasRRHH.main.services.FamiliarService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/familiares")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FamiliarController {

    private final FamiliarService familiarService;

    @GetMapping
    public ResponseEntity<List<Familiar>> listar() {

        return ResponseEntity.ok(familiarService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Familiar> buscarPorId(@PathVariable Long id) {

        return familiarService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Familiar> guardar(
            @Valid @RequestBody Familiar familiar) {

        Familiar nuevoFamiliar = familiarService.guardar(familiar);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nuevoFamiliar);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Familiar> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Familiar familiar) {

        try {

            Familiar familiarActualizado =
                    familiarService.actualizar(id, familiar);

            return ResponseEntity.ok(familiarActualizado);

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        try {

            familiarService.eliminar(id);

            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }
}