package com.SaasRRHH.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.SaasRRHH.main.model.ReporteDiario;
import com.SaasRRHH.main.services.ReporteDiarioService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reportes")
public class ReporteDiarioController {

    @Autowired
    private ReporteDiarioService service;

    
    @GetMapping
    public List<ReporteDiario> listar() {
        return service.listar();
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<ReporteDiario> obtener(@PathVariable Long id) {
        Optional<ReporteDiario> r = service.obtenerPorId(id);
        return r.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    
    @PostMapping
    public ResponseEntity<ReporteDiario> crear(@Valid @RequestBody ReporteDiario reporte) {
        return ResponseEntity.ok(service.guardar(reporte));
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<ReporteDiario> actualizar(@PathVariable Long id,
                                                    @Valid @RequestBody ReporteDiario datos) {
        try {
            return ResponseEntity.ok(service.actualizar(id, datos));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
