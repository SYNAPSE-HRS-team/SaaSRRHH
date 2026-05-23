package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.ValidacionSeguridad;
import com.SaasRRHH.main.services.ValidacionSeguridadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/validaciones_seguridad")
public class ValidacionSeguridadController {
    private final ValidacionSeguridadService service;

    public ValidacionSeguridadController(ValidacionSeguridadService service) {
        this.service = service;
    }

    @GetMapping
    public List<ValidacionSeguridad> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ValidacionSeguridad> obtener(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ValidacionSeguridad crear(@RequestBody ValidacionSeguridad validacionSeguridad) {
        return service.guardar(validacionSeguridad);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ValidacionSeguridad> actualizar(@PathVariable Long id,
                                                           @RequestBody ValidacionSeguridad validacionSeguridad) {
        return service.actualizar(id, validacionSeguridad)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
