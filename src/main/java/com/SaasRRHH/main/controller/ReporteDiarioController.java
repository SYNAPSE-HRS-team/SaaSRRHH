package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.ReporteDiario;
import com.SaasRRHH.main.services.ReporteDiarioService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes-diarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReporteDiarioController {

    private final ReporteDiarioService reporteDiarioService;

    @GetMapping
    public ResponseEntity<List<ReporteDiario>> listar() {

        return ResponseEntity.ok(reporteDiarioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReporteDiario> buscarPorId(@PathVariable Long id) {

        return reporteDiarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ReporteDiario> guardar(
            @Valid @RequestBody ReporteDiario reporteDiario) {

        ReporteDiario nuevoReporte =
                reporteDiarioService.guardar(reporteDiario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nuevoReporte);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReporteDiario> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ReporteDiario reporteDiario) {

        try {

            ReporteDiario reporteActualizado =
                    reporteDiarioService.actualizar(id, reporteDiario);

            return ResponseEntity.ok(reporteActualizado);

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        try {

            reporteDiarioService.eliminar(id);

            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }
}