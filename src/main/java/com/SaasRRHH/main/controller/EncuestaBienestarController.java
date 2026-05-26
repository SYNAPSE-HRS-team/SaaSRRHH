package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.EncuestaBienestarRequestDTO;
import com.SaasRRHH.main.DTO.EncuestaBienestarResponseDTO;
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
    public List<EncuestaBienestarResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public EncuestaBienestarResponseDTO obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public EncuestaBienestarResponseDTO crear(@RequestBody EncuestaBienestarRequestDTO encuesta) {
        return service.guardar(encuesta);
    }

    @PutMapping("/{id}")
    public EncuestaBienestarResponseDTO actualizar(@PathVariable Long id, @RequestBody EncuestaBienestarRequestDTO encuesta) {
        return service.actualizar(id, encuesta);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}