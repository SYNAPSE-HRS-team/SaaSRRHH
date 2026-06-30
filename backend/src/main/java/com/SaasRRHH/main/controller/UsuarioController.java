package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.UsuarioRequestDTO;
import com.SaasRRHH.main.DTO.UsuarioResponseDTO;
import com.SaasRRHH.main.services.UsuarioService;
import com.SaasRRHH.main.security.JwtUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH,
        RequestMethod.DELETE, RequestMethod.OPTIONS })
public class UsuarioController {

    private final UsuarioService service;
    private final JwtUtil jwtUtil;

    public UsuarioController(
            UsuarioService service,
            JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtener(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@RequestBody UsuarioRequestDTO dto) {
        try {
            UsuarioResponseDTO usuario = service.guardar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(service.buscarPorEmail(email));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/ultimo-acceso")
    public ResponseEntity<UsuarioResponseDTO> registrarUltimoAcceso(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.actualizarUltimoAcceso(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ===================================
    // CONSULTAS JPQL
    // ===================================

    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioResponseDTO>> listarActivos() {
        return ResponseEntity.ok(service.listarUsuariosActivos());
    }

    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarPorRol(@PathVariable String rol) {
        return ResponseEntity.ok(service.buscarPorRol(rol));
    }

    @GetMapping("/acceso-reciente")
    public ResponseEntity<List<UsuarioResponseDTO>> accesoReciente(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha) {
        return ResponseEntity.ok(service.usuariosConAccesoReciente(fecha));
    }

    @GetMapping("/estadisticas/roles")
    public ResponseEntity<List<Object[]>> contarUsuariosPorRol() {
        return ResponseEntity.ok(service.contarUsuariosPorRol());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(
            @PathVariable Long id,
            @RequestBody UsuarioRequestDTO dto) {
        try {
            UsuarioResponseDTO usuarioActualizado = service.actualizar(id, dto);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/sin-empleado")
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuariosSinEmpleado() {
        return ResponseEntity.ok(service.listarUsuariosSinEmpleado());
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<?> actualizarPerfil(
            @PathVariable Long id,
            @RequestBody Map<String, String> profileData) {
        try {
            UsuarioResponseDTO usuario = service.actualizarPerfil(id, profileData);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ===================================
    // ENDPOINT PERFIL - Obtener datos del usuario autenticado
    // ===================================

    @GetMapping("/profile")
    public ResponseEntity<UsuarioResponseDTO> getCurrentUserProfile(
            @RequestHeader(value = "Authorization", required = true) String authHeader) {
        try {
            // Extraer el token del header
            String token = authHeader.replace("Bearer ", "");

            // Extraer el email del token
            String email = jwtUtil.extractUsername(token);

            // Buscar el usuario por email
            UsuarioResponseDTO user = service.buscarPorEmail(email);

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}