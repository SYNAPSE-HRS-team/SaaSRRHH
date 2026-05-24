package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.TipoDocumentoRequestDTO;
import com.SaasRRHH.main.DTO.TipoDocumentoResponseDTO;
import com.SaasRRHH.main.services.TipoDocumentoService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-documento")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TipoDocumentoController {

    private final TipoDocumentoService service;

    @GetMapping
    public ResponseEntity<List<TipoDocumentoResponseDTO>>
    listar() {

        return ResponseEntity.ok(
                service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoDocumentoResponseDTO>
    buscarPorId(
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
    public ResponseEntity<TipoDocumentoResponseDTO>
    crear(
            @RequestBody
            TipoDocumentoRequestDTO dto) {

        try {

            TipoDocumentoResponseDTO response =
                    service.guardar(dto);

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
    public ResponseEntity<TipoDocumentoResponseDTO>
    actualizar(
            @PathVariable Long id,
            @RequestBody
            TipoDocumentoRequestDTO dto) {

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
    public ResponseEntity<Void>
    eliminar(
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

    // ==================================
    // CONSULTAS
    // ==================================

    @GetMapping("/obligatorios")
    public ResponseEntity<List<TipoDocumentoResponseDTO>>
    listarObligatorios() {

        return ResponseEntity.ok(
                service.listarObligatorios());
    }

    @GetMapping("/renovables")
    public ResponseEntity<List<TipoDocumentoResponseDTO>>
    listarRenovables() {

        return ResponseEntity.ok(
                service.listarRenovables());
    }

    @GetMapping("/vigencia")
    public ResponseEntity<List<TipoDocumentoResponseDTO>>
    listarPorVigencia() {

        return ResponseEntity.ok(
                service.listarPorVigencia());
    }

    @GetMapping("/estadisticas/obligatorios")
    public ResponseEntity<Long>
    contarObligatorios() {

        return ResponseEntity.ok(
                service.contarObligatorios());
    }
}