package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.AreaTrabajo;
import com.SaasRRHH.main.services.AreaTrabajoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/areas_trabajo")
public class AreaTrabajoController {
    private final AreaTrabajoService service;

    public AreaTrabajoController(AreaTrabajoService service) {
        this.service = service;
    }

    @GetMapping
    public List<AreaTrabajo> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AreaTrabajo> obtener(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public AreaTrabajo crear(@RequestBody AreaTrabajo areaTrabajo) {
        return service.guardar(areaTrabajo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AreaTrabajo> actualizar(@PathVariable Long id, @RequestBody AreaTrabajo areaTrabajo) {
        return service.actualizar(id, areaTrabajo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
