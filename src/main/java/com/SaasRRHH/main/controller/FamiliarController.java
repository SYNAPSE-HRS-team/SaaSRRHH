package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.FamiliarDTO;
import com.SaasRRHH.main.services.FamiliarService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/familiares")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FamiliarController {

    private final FamiliarService familiarService;

    @GetMapping
    public ResponseEntity<List<FamiliarDTO>> listar() {
        return ResponseEntity.ok(familiarService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FamiliarDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(familiarService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<FamiliarDTO> guardar(@Valid @RequestBody FamiliarDTO dto) {
        return ResponseEntity.ok(familiarService.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FamiliarDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody FamiliarDTO dto) {

        return ResponseEntity.ok(familiarService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        familiarService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<FamiliarDTO>> buscarPorEmpleado(@PathVariable Long empleadoId) {

        List<FamiliarDTO> lista = familiarService.findByEmpleadoId(empleadoId);

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }
}