package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.AreaTrabajo;
import com.SaasRRHH.main.services.AreaTrabajoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/areas-trabajo")
@CrossOrigin(origins = "*")
public class AreaTrabajoController {

    private final AreaTrabajoService service;

    public AreaTrabajoController(AreaTrabajoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AreaTrabajo>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/activas")
    public ResponseEntity<List<AreaTrabajo>> listarActivas() {
        return ResponseEntity.ok(service.listarActivas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AreaTrabajo> obtener(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AreaTrabajo> crear(@RequestBody AreaTrabajo area) {
        try {
            AreaTrabajo nuevaArea = service.guardar(area);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaArea);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AreaTrabajo> actualizar(@PathVariable Long id, @RequestBody AreaTrabajo area) {
        return service.buscarPorId(id)
                .map(existing -> {
                    area.setId(id);
                    AreaTrabajo actualizada = service.guardar(area);
                    return ResponseEntity.ok(actualizada);
                })
                .orElse(ResponseEntity.notFound().build());
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

    @GetMapping("/buscar")
    public ResponseEntity<AreaTrabajo> buscarPorNombre(@RequestParam String nombre) {
        return service.buscarPorNombre(nombre)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}