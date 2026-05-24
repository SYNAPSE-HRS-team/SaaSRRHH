package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.EmpleadoRequestDTO;
import com.SaasRRHH.main.DTO.EmpleadoResponseDTO;
import com.SaasRRHH.main.services.EmpleadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


    @RestController
    @RequestMapping("/api/empleados")
    @RequiredArgsConstructor
    public class EmpleadoController {

        private final EmpleadoService service;

        @GetMapping
        public List<EmpleadoResponseDTO> listar() {
            return service.listar();
        }

        @GetMapping("/{id}")
        public EmpleadoResponseDTO obtener(@PathVariable Long id) {
            return service.buscarPorId(id);
        }

        @GetMapping("/dni/{dni}")
        public EmpleadoResponseDTO buscarPorDni(@PathVariable String dni) {
            return service.buscarPorDni(dni);
        }

        @GetMapping("/activos")
        public List<EmpleadoResponseDTO> listarActivos() {
            return service.listarActivos();
        }

        @PostMapping
        public EmpleadoResponseDTO crear(@RequestBody EmpleadoRequestDTO dto) {
            return service.guardar(dto);
        }

        @DeleteMapping("/{id}")
        public void eliminar(@PathVariable Long id) {
            service.eliminar(id);
        }
    }