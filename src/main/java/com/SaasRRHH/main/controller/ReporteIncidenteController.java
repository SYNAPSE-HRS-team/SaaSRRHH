package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.ReporteIncidente;
import com.SaasRRHH.main.services.ReporteIncidenteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes_incidentes")
public class ReporteIncidenteController {

    private final ReporteIncidenteService service;

    public ReporteIncidenteController(ReporteIncidenteService service) {
        this.service = service;
    }

    @GetMapping
    public List<ReporteIncidente> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ReporteIncidente obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public ReporteIncidente crear(@RequestBody ReporteIncidente data) {
        return service.guardar(data);
    }

    @PutMapping("/{id}")
    public ReporteIncidente actualizar(@PathVariable Long id, @RequestBody ReporteIncidente data) {
        return service.actualizar(id, data);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}