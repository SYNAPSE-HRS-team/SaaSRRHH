package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.TareaAsignada;
import com.SaasRRHH.main.services.TareaAsignadaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tareas_asignadas")
public class TareaAsignadaController {
    private final TareaAsignadaService service;

    public TareaAsignadaController(TareaAsignadaService service) {
        this.service = service;
    }

    @GetMapping
    public List<TareaAsignada> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TareaAsignada> obtener(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public TareaAsignada crear(@RequestBody TareaAsignada tareaAsignada) {
        return service.guardar(tareaAsignada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TareaAsignada> actualizar(@PathVariable Long id, @RequestBody TareaAsignada tareaAsignada) {
        return service.actualizar(id, tareaAsignada)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
