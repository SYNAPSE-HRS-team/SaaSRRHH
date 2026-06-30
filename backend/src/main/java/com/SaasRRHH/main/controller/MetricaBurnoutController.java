package com.SaasRRHH.main.controller;
import com.SaasRRHH.main.model.MetricaBurnout;
import com.SaasRRHH.main.services.MetricaBurnoutService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/burnout")
public class MetricaBurnoutController {

    private final MetricaBurnoutService metricaBurnoutService;


    // Dependency Injection por constructor
    public MetricaBurnoutController(
            MetricaBurnoutService metricaBurnoutService) {
        this.metricaBurnoutService = metricaBurnoutService;
    }


    // ==========================
    // GET ALL
    // ==========================
    @GetMapping
    public ResponseEntity<List<MetricaBurnout>> listarMetricas() {

        return ResponseEntity.ok(
                metricaBurnoutService.listar()
        );
    }


    // ==========================
    // GET BY ID
    // ==========================
    @GetMapping("/{id}")
    public ResponseEntity<MetricaBurnout> obtenerPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                metricaBurnoutService.obtenerPorId(id)
        );
    }


    // ==========================
    // GET BY EMPLEADO
    // ==========================
    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<MetricaBurnout>> buscarPorEmpleado(
            @PathVariable Long empleadoId) {

        return ResponseEntity.ok(
                metricaBurnoutService.buscarPorEmpleado(empleadoId)
        );
    }


    // ==========================
    // POST
    // ==========================
    @PostMapping
    public ResponseEntity<MetricaBurnout> crearMetrica(
            @RequestBody MetricaBurnout metrica) {

        MetricaBurnout nueva =
                metricaBurnoutService.guardar(metrica);

        return new ResponseEntity<>(
                nueva,
                HttpStatus.CREATED
        );
    }


    // ==========================
    // PUT
    // ==========================
    @PutMapping("/{id}")
    public ResponseEntity<MetricaBurnout> actualizar(
            @PathVariable Long id,
            @RequestBody MetricaBurnout metrica) {

        return ResponseEntity.ok(
                metricaBurnoutService.actualizar(
                        id,
                        metrica
                )
        );
    }


    // ==========================
    // DELETE
    // ==========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        metricaBurnoutService.eliminar(id);

        return ResponseEntity.noContent().build();
    }

}
