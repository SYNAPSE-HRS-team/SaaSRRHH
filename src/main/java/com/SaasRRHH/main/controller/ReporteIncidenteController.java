package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.ReporteIncidenteRequestDTO;
import com.SaasRRHH.main.DTO.ReporteIncidenteResponseDTO;
import com.SaasRRHH.main.services.ReporteIncidenteService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reportes-incidentes")
@RequiredArgsConstructor
public class ReporteIncidenteController {

    private final ReporteIncidenteService service;


    @GetMapping
    public List<ReporteIncidenteResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ReporteIncidenteResponseDTO obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public ReporteIncidenteResponseDTO crear(@RequestBody ReporteIncidenteRequestDTO dto) {
        return service.guardar(dto);
    }

    @PutMapping("/{id}")
    public ReporteIncidenteResponseDTO actualizar(
            @PathVariable Long id,
            @RequestBody ReporteIncidenteRequestDTO dto) {

        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }




    @GetMapping("/empleado/{id}")
    public List<ReporteIncidenteResponseDTO> porEmpleado(@PathVariable Long id) {
        return service.listarPorEmpleado(id);
    }

    @GetMapping("/rango")
    public List<ReporteIncidenteResponseDTO> porRango(
            @RequestParam LocalDateTime inicio,
            @RequestParam LocalDateTime fin) {

        return service.buscarPorRangoFechas(inicio, fin);
    }

    @GetMapping("/riesgo/{nivel}")
    public List<ReporteIncidenteResponseDTO> porRiesgo(@PathVariable String nivel) {
        return service.listarPorNivelRiesgo(nivel);
    }

    @GetMapping("/estado/{estado}")
    public List<ReporteIncidenteResponseDTO> porEstado(@PathVariable String estado) {
        return service.listarPorEstado(estado);
    }

    @GetMapping("/criticos")
    public List<ReporteIncidenteResponseDTO> criticos() {
        return service.incidentesCriticos();
    }

    @GetMapping("/hoy")
    public List<ReporteIncidenteResponseDTO> deHoy() {
        return service.incidentesDeHoy();
    }

    @GetMapping("/criticos-detalle")
    public List<ReporteIncidenteResponseDTO> criticosDetalle() {
        return service.incidentesCriticosConDetalle();
    }




    @GetMapping("/stats/empleado")
    public List<Object[]> porEmpleado() {
        return service.incidentesPorEmpleado();
    }

    @GetMapping("/stats/riesgo")
    public List<Object[]> porRiesgo() {
        return service.incidentesPorRiesgo();
    }

    @GetMapping("/stats/area")
    public List<Object[]> porArea() {
        return service.incidentesPorArea();
    }

    @GetMapping("/stats/supervisor")
    public List<Object[]> porSupervisor() {
        return service.incidentesPorSupervisor();
    }
}