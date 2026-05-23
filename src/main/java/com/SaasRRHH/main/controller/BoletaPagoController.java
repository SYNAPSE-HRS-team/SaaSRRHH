package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.BoletaPago;
import com.SaasRRHH.main.services.BoletaPagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boletas_pago")
public class BoletaPagoController {

    private final BoletaPagoService service;

    public BoletaPagoController(BoletaPagoService service) {
        this.service = service;
    }

    @GetMapping
    public List<BoletaPago> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoletaPago> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public BoletaPago crear(@RequestBody BoletaPago boleta) {
        return service.guardar(boleta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoletaPago> actualizar(
            @PathVariable Long id,
            @RequestBody BoletaPago data
    ) {
        if (service.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(service.actualizar(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (service.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}