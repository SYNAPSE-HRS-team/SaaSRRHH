package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * POST /api/auth/login
 * Body: { "email": "admin@empresa.com", "password": "123456" }
 * Response: { "token": "eyJ..." }
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

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

        return ResponseEntity.ok(Map.of(
            "token", token,
            "email", userDetails.getUsername(),
            "roles", userDetails.getAuthorities()
                       .stream()
                       .map(a -> a.getAuthority())
                       .toList()
        ));
    }


    

    // DTO interno (record de Java 16+)
    public record LoginRequestDTO(String email, String password) {}
}
