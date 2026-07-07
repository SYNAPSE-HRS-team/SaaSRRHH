package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.DocumentoPrivadoRequestDTO;
import com.SaasRRHH.main.DTO.DocumentoPrivadoResponseDTO;
import com.SaasRRHH.main.DTO.TipoDocumentoResponseDTO;
import com.SaasRRHH.main.services.DocumentoPrivadoService;
import com.SaasRRHH.main.services.FileStorageService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documentos-privados")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DocumentoPrivadoController {

        private final DocumentoPrivadoService service;
        private final FileStorageService fileStorageService;

        // ===================================
        // SUBIDA DE ARCHIVO (paso previo al guardar)
        // ===================================

        @PostMapping("/upload")
        public ResponseEntity<Map<String, String>> subirArchivo(
                        @RequestParam("file") MultipartFile file) {

                try {
                        String url = fileStorageService.guardar(file);
                        return ResponseEntity.ok(Map.of("url", url));

                } catch (Exception e) {
                        return ResponseEntity
                                        .badRequest()
                                        .body(Map.of("error", "No se pudo guardar el archivo: " + e.getMessage()));
                }
        }

        // ===================================
        // CRUD
        // ===================================

        @GetMapping
        public ResponseEntity<List<DocumentoPrivadoResponseDTO>> listar() {

                return ResponseEntity.ok(
                                service.listar());
        }

        @GetMapping("/{id}")
        public ResponseEntity<DocumentoPrivadoResponseDTO> buscarPorId(
                        @PathVariable Long id) {

                try {

                        return ResponseEntity.ok(
                                        service.buscarPorId(id));

                } catch (RuntimeException e) {

                        return ResponseEntity
                                        .notFound()
                                        .build();
                }
        }

        @PostMapping
        public ResponseEntity<DocumentoPrivadoResponseDTO> guardar(

                        @Valid @RequestBody DocumentoPrivadoRequestDTO dto) {

                try {

                        DocumentoPrivadoResponseDTO response = service.guardar(dto);

                        return ResponseEntity
                                        .status(HttpStatus.CREATED)
                                        .body(response);

                } catch (RuntimeException e) {

                        return ResponseEntity
                                        .badRequest()
                                        .build();
                }
        }

        @PutMapping("/{id}")
        public ResponseEntity<DocumentoPrivadoResponseDTO> actualizar(

                        @PathVariable Long id,

                        @Valid @RequestBody DocumentoPrivadoRequestDTO dto) {

                try {

                        return ResponseEntity.ok(
                                        service.actualizar(id, dto));

                } catch (RuntimeException e) {

                        return ResponseEntity
                                        .notFound()
                                        .build();
                }
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> eliminar(
                        @PathVariable Long id) {

                try {

                        service.eliminar(id);

                        return ResponseEntity
                                        .noContent()
                                        .build();

                } catch (RuntimeException e) {

                        return ResponseEntity
                                        .notFound()
                                        .build();
                }
        }

        // ===================================
        // CONSULTAS
        // ===================================

        @GetMapping("/activos")
        public ResponseEntity<List<DocumentoPrivadoResponseDTO>> listarActivos() {

                return ResponseEntity.ok(
                                service.listarActivos());
        }

        @GetMapping("/empleado/{empleadoId}")
        public ResponseEntity<List<DocumentoPrivadoResponseDTO>> buscarPorEmpleado(
                        @PathVariable Long empleadoId) {

                return ResponseEntity.ok(
                                service.buscarPorEmpleado(
                                                empleadoId));
        }

        @GetMapping("/buscar-fecha-emision")
        public ResponseEntity<List<DocumentoPrivadoResponseDTO>> buscarFechaEmision(@RequestParam("fecha")
                                                                                            @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
                                                                                            LocalDate fecha){
                return  ResponseEntity.ok(service.buscarPorFechaEmision(fecha));
        }


        @GetMapping("/tipo/{tipoId}")
        public ResponseEntity<List<DocumentoPrivadoResponseDTO>> buscarPorTipo(
                        @PathVariable Long tipoId) {

                return ResponseEntity.ok(
                                service.buscarPorTipo(
                                                tipoId));
        }

        @GetMapping("/activos-relaciones")
        public ResponseEntity<List<DocumentoPrivadoResponseDTO>> listarActivosConRelaciones() {

                return ResponseEntity.ok(
                                service.listarActivosConRelaciones());
        }

        @GetMapping("/vencidos")
        public ResponseEntity<List<DocumentoPrivadoResponseDTO>> documentosVencidos() {

                return ResponseEntity.ok(
                                service.documentosVencidos());
        }

        @GetMapping("/por-vencer")
        public ResponseEntity<List<DocumentoPrivadoResponseDTO>> documentosPorVencer(

                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaLimite) {

                return ResponseEntity.ok(
                                service.documentosPorVencer(
                                                fechaLimite));
        }

        @GetMapping("/estadisticas/tipos")
        public ResponseEntity<List<Object[]>> contarDocumentosPorTipo() {

                return ResponseEntity.ok(
                                service.contarDocumentosPorTipo());
        }

        @GetMapping("/estadisticas/empleados")
        public ResponseEntity<List<Object[]>> empleadosConMasDocumentos() {

                return ResponseEntity.ok(
                                service.empleadosConMasDocumentos());
        }
}