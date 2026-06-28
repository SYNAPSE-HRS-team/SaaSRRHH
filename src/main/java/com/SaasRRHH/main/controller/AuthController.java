package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.UsuarioRequestDTO;
import com.SaasRRHH.main.DTO.UsuarioResponseDTO;
import com.SaasRRHH.main.model.Usuario;
import com.SaasRRHH.main.repository.UsuarioRepository;
import com.SaasRRHH.main.security.JwtUtil;
import com.SaasRRHH.main.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.email(), request.password()
                )
            );
        } catch (Exception ex) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Credenciales inválidas"));
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        String token = jwtUtil.generateToken(userDetails);

        // Buscar usuario en BD para obtener id y rol
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String rol = usuario.getRol().getNombreRol();

        return ResponseEntity.ok(Map.of(
            "token", token,
            "email", userDetails.getUsername(),
            "rol", rol,
            "idUsuario", usuario.getId(),
            "tipo", "Bearer"
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        try {
            if (usuarioRepository.existsByEmail(request.email())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "El email ya está registrado"));
            }

            UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setEmail(request.email());
            dto.setPassword(passwordEncoder.encode(request.password()));
            dto.setRolId(request.rolId());
            dto.setActivo(true);

            UsuarioResponseDTO usuario = usuarioService.guardar(dto);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                        "message", "Usuario creado exitosamente",
                        "id", usuario.getId(),
                        "email", usuario.getEmail()
                    ));
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", ex.getMessage() != null ? ex.getMessage() : "Error al crear el usuario"));
        }
    }

    public record LoginRequestDTO(String email, String password) {}
    public record RegisterRequestDTO(String email, String password, Long rolId) {}
}
