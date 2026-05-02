package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.Rol; 
import com.SaasRRHH.main.services.RolService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService service;

    public RolController(RolService service) {
        this.service = service;
    }

    @GetMapping
    public List<Rol> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rol> obtener(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Rol crear(@RequestBody Rol rol) {
        return service.guardar(rol);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    @GetMapping("/buscar")
    public ResponseEntity<Rol> buscarPorNombre(@RequestParam String nombre) {
        return service.buscarPorNombre(nombre)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
