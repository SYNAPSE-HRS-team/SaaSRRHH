package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.UsuarioRequestDTO;
import com.SaasRRHH.main.DTO.UsuarioResponseDTO;
import com.SaasRRHH.main.model.Usuario;
import com.SaasRRHH.main.repository.UsuarioRepository;
import com.SaasRRHH.main.security.JwtUtil;
import com.SaasRRHH.main.services.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(), request.password()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        String token = jwtUtil.generateToken(userDetails);

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtener el nombre del rol correctamente
        String rol = usuario.getRol().getNombreRol();

        // Construir respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("tipo", "Bearer");
        response.put("email", userDetails.getUsername());
        response.put("rol", rol);
        response.put("idUsuario", usuario.getId());
        response.put("nombre", usuario.getNombre() != null ? usuario.getNombre() : "");
        response.put("apellido", usuario.getApellido() != null ? usuario.getApellido() : "");
        response.put("telefono", usuario.getTelefono() != null ? usuario.getTelefono() : "");

        // Opcional: si quieres incluir el nombre completo
        String nombreCompleto = usuario.getNombre() + " " + usuario.getApellido();
        response.put("nombreCompleto", nombreCompleto.trim());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            // Validar si el email ya existe
            if (usuarioRepository.existsByEmail(request.email())) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("message", "El email ya está registrado"));
            }

            // Crear DTO para el servicio
            UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setEmail(request.email());
            dto.setPassword(passwordEncoder.encode(request.password()));
            dto.setRolId(request.rolId());
            dto.setActivo(true);

            // Datos adicionales del registro (si se proporcionan)
            if (request.nombre() != null) {
                dto.setNombre(request.nombre());
            }
            if (request.apellido() != null) {
                dto.setApellido(request.apellido());
            }
            if (request.telefono() != null) {
                dto.setTelefono(request.telefono());
            }

            // Guardar usuario
            UsuarioResponseDTO usuario = usuarioService.guardar(dto);

            // Respuesta exitosa
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuario creado exitosamente");
            response.put("id", usuario.getId());
            response.put("email", usuario.getEmail());
            response.put("nombre", usuario.getNombre() != null ? usuario.getNombre() : "");
            response.put("apellido", usuario.getApellido() != null ? usuario.getApellido() : "");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", ex.getMessage() != null ? ex.getMessage() : "Error al crear el usuario"));
        }
    }

    // ============================================
    // RECORDS (DTOs)
    // ============================================

    public record LoginRequestDTO(
            @NotBlank(message = "El correo electrónico no puede estar vacío") @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Formato de correo electrónico no válido") String email,

            @NotBlank(message = "La contraseña no puede estar vacía") String password) {
    }

    public record RegisterRequestDTO(
            @NotBlank(message = "El correo electrónico no puede estar vacío") @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Formato de correo electrónico no válido") String email,

            @NotBlank(message = "La contraseña no puede estar vacía") String password,

            Long rolId,

            // 👇 Campos opcionales para registro
            String nombre,
            String apellido,
            String telefono) {
    }
}