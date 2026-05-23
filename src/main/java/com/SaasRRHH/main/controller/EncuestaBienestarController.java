package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.Encuestabienestar;
import com.SaasRRHH.main.services.EncuestaBienestarService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/encuestas_bienestar")
public class EncuestaBienestarController {

    private final EncuestaBienestarService service;

    public EncuestaBienestarController(EncuestaBienestarService service) {
        this.service = service;
    }

    @GetMapping
    public List<Encuestabienestar> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Encuestabienestar obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public Encuestabienestar crear(@RequestBody Encuestabienestar encuesta) {
        return service.guardar(encuesta);
    }

    @PutMapping("/{id}")
    public Encuestabienestar actualizar(@PathVariable Long id, @RequestBody Encuestabienestar encuesta) {
        return service.actualizar(id, encuesta);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}