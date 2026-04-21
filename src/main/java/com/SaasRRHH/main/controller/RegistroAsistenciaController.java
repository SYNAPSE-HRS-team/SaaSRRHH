package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.entity.RegistroAsistencia;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


import com.SaasRRHH.main.services.RegistroAsistenciaService;
import java.util.List;

@RestController
@RequestMapping("/api/registros_asistencia")
public class RegistroAsistenciaController {
    private final RegistroAsistenciaService service;

    public RegistroAsistenciaController(RegistroAsistenciaService service) {
        this.service = service;
    }

    @GetMapping
    public List<RegistroAsistencia> listar() {
        return service.listar();
    }

    // 📌 Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<RegistroAsistencia> obtener(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // 📌 Crear Registro

    @PostMapping
    public RegistroAsistencia crear(@RequestBody RegistroAsistencia registroAsistencia) {
        return service.guardar(registroAsistencia);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

}
