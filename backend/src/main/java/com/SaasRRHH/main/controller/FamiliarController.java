package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.FamiliarRequestDTO;
import com.SaasRRHH.main.DTO.FamiliarResponseDTO;
import com.SaasRRHH.main.model.Familiar;
import com.SaasRRHH.main.services.FamiliarService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/familiares")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FamiliarController {

    private final FamiliarService familiarService;

    // ===================================
    // CRUD
    // ===================================

    @GetMapping
    public ResponseEntity<List<FamiliarResponseDTO>>
    listar() {

        return ResponseEntity.ok(
                familiarService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FamiliarResponseDTO>
    buscarPorId(
            @PathVariable Long id) {

        try {

            return ResponseEntity.ok(
                    familiarService.buscarPorId(id));

        } catch (RuntimeException e) {

            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    @PostMapping
    public ResponseEntity<FamiliarResponseDTO>
    guardar(

            @Valid
            @RequestBody
            FamiliarRequestDTO dto) {

        try {

            FamiliarResponseDTO response =
                    familiarService.guardar(dto);

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
    public ResponseEntity<FamiliarResponseDTO>
    actualizar(

            @PathVariable Long id,

            @Valid
            @RequestBody
            FamiliarRequestDTO dto) {

        try {

            return ResponseEntity.ok(
                    familiarService.actualizar(id, dto));

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

            familiarService.eliminar(id);

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

    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<FamiliarResponseDTO>>
    buscarPorEmpleado(
            @PathVariable Long empleadoId) {

        List<FamiliarResponseDTO> lista =
                familiarService.findByEmpleadoId(
                        empleadoId);

        if (lista.isEmpty()) {

            return ResponseEntity
                    .noContent()
                    .build();
        }

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<FamiliarResponseDTO>>
    listarActivos() {

        return ResponseEntity.ok(
                familiarService.listarActivos());
    }

    @GetMapping("/parentesco/{parentesco}")
    public ResponseEntity<List<FamiliarResponseDTO>>
    buscarPorParentesco(

            @PathVariable
            Familiar.Parentesco parentesco) {

        return ResponseEntity.ok(
                familiarService.buscarPorParentesco(
                        parentesco));
    }

    @GetMapping("/estudiantes")
    public ResponseEntity<List<FamiliarResponseDTO>>
    familiaresQueEstudian() {

        return ResponseEntity.ok(
                familiarService.familiaresQueEstudian());
    }

    @GetMapping("/estadisticas/parentescos")
    public ResponseEntity<List<Object[]>>
    contarPorParentesco() {

        return ResponseEntity.ok(
                familiarService.contarPorParentesco());
    }
}
