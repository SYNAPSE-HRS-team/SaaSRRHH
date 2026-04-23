package com.SaasRRHH.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.SaasRRHH.main.model.Familiar;
import com.SaasRRHH.main.services.FamiliarService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/familiares")
public class FamiliarController {

    @Autowired
    private FamiliarService familiarService;

    
    @GetMapping
    public List<Familiar> listar() {
        return familiarService.listar();
    }

  
    @GetMapping("/{id}")
    public ResponseEntity<Familiar> obtenerPorId(@PathVariable Long id) {
        Optional<Familiar> familiar = familiarService.obtenerPorId(id);
        return familiar.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }

    
    @PostMapping
    public ResponseEntity<Familiar> crear(@Valid @RequestBody Familiar familiar) {
        Familiar nuevo = familiarService.guardar(familiar);
        return ResponseEntity.ok(nuevo);
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<Familiar> actualizar(@PathVariable Long id,
                                               @Valid @RequestBody Familiar datos) {
        try {
            Familiar actualizado = familiarService.actualizar(id, datos);
            return ResponseEntity.ok(actualizado);
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