package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.Planilla;
import com.SaasRRHH.main.services.PlanillaService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planillas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlanillaController {

    private final PlanillaService planillaService;

    @GetMapping
    public ResponseEntity<List<Planilla>> listar() {

        return ResponseEntity.ok(planillaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Planilla> buscarPorId(@PathVariable Long id) {

        return planillaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Planilla> guardar(@Valid @RequestBody Planilla planilla) {

        Planilla nuevaPlanilla = planillaService.guardar(planilla);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaPlanilla);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Planilla> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Planilla planilla) {

        try {

            Planilla planillaActualizada =
                    planillaService.actualizar(id, planilla);

            return ResponseEntity.ok(planillaActualizada);

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        try {

            planillaService.eliminar(id);

            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }
}