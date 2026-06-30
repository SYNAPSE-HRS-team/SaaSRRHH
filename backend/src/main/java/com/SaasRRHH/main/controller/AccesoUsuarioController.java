package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.AccesoUsuarioRequestDTO;
import com.SaasRRHH.main.DTO.AccesoUsuarioResponseDTO;
import com.SaasRRHH.main.services.AccesoUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/accesos")
@RequiredArgsConstructor
public class AccesoUsuarioController {

        private final AccesoUsuarioService service;


        @GetMapping
        public ResponseEntity<List<AccesoUsuarioResponseDTO>> listar() {
                return ResponseEntity.ok(service.listar());
        }

        @GetMapping("/{id}")
        public ResponseEntity<AccesoUsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
                return ResponseEntity.ok(service.buscarPorId(id));
        }

        @GetMapping("/usuario/{usuarioId}")
        public ResponseEntity<List<AccesoUsuarioResponseDTO>> porUsuario(@PathVariable Long usuarioId) {
                return ResponseEntity.ok(service.buscarPorUsuario(usuarioId));
        }

        @PostMapping
        public ResponseEntity<AccesoUsuarioResponseDTO> guardar(@RequestBody AccesoUsuarioRequestDTO dto) {
                return new ResponseEntity<>(service.guardar(dto), HttpStatus.CREATED);
        }

        @PutMapping("/{id}")
        public ResponseEntity<AccesoUsuarioResponseDTO> actualizar(
                @PathVariable Long id,
                @RequestBody AccesoUsuarioRequestDTO dto) {

                return ResponseEntity.ok(service.actualizar(id, dto));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> eliminar(@PathVariable Long id) {
                service.eliminar(id);
                return ResponseEntity.noContent().build();
        }


        @GetMapping("/ordenados/{usuarioId}")
        public ResponseEntity<List<AccesoUsuarioResponseDTO>> accesosOrdenados(
                @PathVariable Long usuarioId) {

                return ResponseEntity.ok(service.listarOrdenadosPorUsuario(usuarioId));
        }

        @GetMapping("/rango")
        public ResponseEntity<List<AccesoUsuarioResponseDTO>> porRangoFechas(
                @RequestParam LocalDateTime inicio,
                @RequestParam LocalDateTime fin) {

                return ResponseEntity.ok(service.buscarPorRangoFechas(inicio, fin));
        }


        @GetMapping("/fallidos")
        public ResponseEntity<List<AccesoUsuarioResponseDTO>> fallidos() {
                return ResponseEntity.ok(service.listarFallidos());
        }

        @GetMapping("/fallidos-detalle")
        public ResponseEntity<List<AccesoUsuarioResponseDTO>> fallidosConUsuario() {
                return ResponseEntity.ok(service.listarFallidosConUsuario());
        }



        @GetMapping("/activos")
        public ResponseEntity<List<AccesoUsuarioResponseDTO>> sesionesActivas() {
                return ResponseEntity.ok(service.sesionesActivas());
        }

        @GetMapping("/ultimo-acceso/{usuarioId}")
        public ResponseEntity<List<AccesoUsuarioResponseDTO>> ultimoAcceso(
                @PathVariable Long usuarioId) {

                return ResponseEntity.ok(service.ultimoAccesoUsuario(usuarioId));
        }


        @GetMapping("/top-usuarios")
        public ResponseEntity<List<Object[]>> usuariosMasActivos() {
                return ResponseEntity.ok(service.usuariosMasActivos());
        }

        @GetMapping("/exitosos")
        public ResponseEntity<List<Object[]>> accesosExitosos() {
                return ResponseEntity.ok(service.accesosExitososPorUsuario());
        }
}