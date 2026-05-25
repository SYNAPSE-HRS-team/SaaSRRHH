package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.ReporteDiarioRequestDTO;
import com.SaasRRHH.main.DTO.ReporteDiarioResponseDTO;
import com.SaasRRHH.main.services.ReporteDiarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reportes-diarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReporteDiarioController {

    private final ReporteDiarioService service;


    @GetMapping
    public ResponseEntity<List<ReporteDiarioResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReporteDiarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ReporteDiarioResponseDTO> guardar(
            @Valid @RequestBody ReporteDiarioRequestDTO dto) {

        return new ResponseEntity<>(
                service.guardar(dto),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReporteDiarioResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ReporteDiarioRequestDTO dto) {

        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }



    @GetMapping("/rango")
    public ResponseEntity<List<ReporteDiarioResponseDTO>> porRango(
            @RequestParam LocalDateTime inicio,
            @RequestParam LocalDateTime fin) {

        return ResponseEntity.ok(service.buscarPorRangoFechas(inicio, fin));
    }

    @GetMapping("/empleado/{id}")
    public ResponseEntity<List<ReporteDiarioResponseDTO>> porEmpleado(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorEmpleado(id));
    }

    @GetMapping("/tarea/{id}")
    public ResponseEntity<List<ReporteDiarioResponseDTO>> porTarea(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorTarea(id));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ReporteDiarioResponseDTO>> porEstado(@PathVariable String estado) {
        return ResponseEntity.ok(service.listarPorEstado(estado));
    }

    @GetMapping("/bajo-avance")
    public ResponseEntity<List<ReporteDiarioResponseDTO>> bajoAvance() {
        return ResponseEntity.ok(service.reportesBajoAvance());
    }

    @GetMapping("/hoy")
    public ResponseEntity<List<ReporteDiarioResponseDTO>> reportesDeHoy() {
        return ResponseEntity.ok(service.reportesDeHoy());
    }



    @GetMapping("/reportes-por-empleado")
    public ResponseEntity<List<Object[]>> reportesPorEmpleado() {
        return ResponseEntity.ok(service.reportesPorEmpleado());
    }

    @GetMapping("/avance-promedio")
    public ResponseEntity<List<Object[]>> avancePromedio() {
        return ResponseEntity.ok(service.avancePromedioPorTarea());
    }
}