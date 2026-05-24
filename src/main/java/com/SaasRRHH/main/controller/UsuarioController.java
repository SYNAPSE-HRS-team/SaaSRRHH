package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.UsuarioRequestDTO;
import com.SaasRRHH.main.DTO.UsuarioResponseDTO;
import com.SaasRRHH.main.services.UsuarioService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(
            UsuarioService service) {

        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>>
    listar() {

        return ResponseEntity.ok(
                service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO>
    obtener(@PathVariable Long id) {

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
    public ResponseEntity<UsuarioResponseDTO>
    crear(@RequestBody UsuarioRequestDTO dto) {

        try {

            UsuarioResponseDTO usuario =
                    service.guardar(dto);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(usuario);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    eliminar(@PathVariable Long id) {

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

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResponseDTO>
    buscarPorEmail(
            @PathVariable String email) {

        try {

            return ResponseEntity.ok(
                    service.buscarPorEmail(email));

        } catch (RuntimeException e) {

            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    @PatchMapping("/{id}/ultimo-acceso")
    public ResponseEntity<UsuarioResponseDTO>
    registrarUltimoAcceso(
            @PathVariable Long id) {

        try {

            return ResponseEntity.ok(
                    service.actualizarUltimoAcceso(id));

        } catch (RuntimeException e) {

            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    // ===================================
    // CONSULTAS JPQL
    // ===================================

    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioResponseDTO>>
    listarActivos() {

        return ResponseEntity.ok(
                service.listarUsuariosActivos());
    }

    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioResponseDTO>>
    buscarPorRol(
            @PathVariable String rol) {

        return ResponseEntity.ok(
                service.buscarPorRol(rol));
    }

    @GetMapping("/acceso-reciente")
    public ResponseEntity<List<UsuarioResponseDTO>>
    accesoReciente(

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fecha) {

        return ResponseEntity.ok(
                service.usuariosConAccesoReciente(
                        fecha));
    }

    @GetMapping("/estadisticas/roles")
    public ResponseEntity<List<Object[]>>
    contarUsuariosPorRol() {

        return ResponseEntity.ok(
                service.contarUsuariosPorRol());
    }
}