package com.SaasRRHH.main.config;

import com.SaasRRHH.main.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // habilita @PreAuthorize en controllers
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    // -------------------------------------------------------
    // PasswordEncoder — BCrypt
    // -------------------------------------------------------
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // -------------------------------------------------------
    // AuthenticationManager — necesario para el AuthController
    // -------------------------------------------------------
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // -------------------------------------------------------
    // Cadena de filtros principal
    // -------------------------------------------------------
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sess ->
                    sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // ── Rutas públicas ─────────────────────────────────────
                .requestMatchers("/api/auth/**").permitAll()

                // ── Solo ADMIN ─────────────────────────────────────────
                .requestMatchers(
                    "/api/usuarios/**",
                    "/api/roles/**",
                    "/api/nomina/**",
                    "/api/planillas/**",
                    "/api/boletas_pago/**",
                    "/api/analitica/**",
                    "/api/dispositivos-autorizados/**",
                    "/api/accesos/**",
                    "/api/validaciones-seguridad/**"
                ).hasRole("ADMIN")

                // ── ADMIN o SUPERVISOR ─────────────────────────────────
                .requestMatchers(
                    "/api/empleados/**",
                    "/api/tareas-asignadas/**",
                    "/api/reportes-incidentes/**",
                    "/api/reportes-diarios/**",
                    "/api/asistencias/**",
                    "/api/documentos-privados/**",
                    "/api/tipos-documento/**",
                    "/api/areas-trabajo/**",
                    "/api/burnout/**"
                ).hasAnyRole("ADMIN", "SUPERVISOR")

                // ── Cualquier usuario autenticado ──────────────────────
                .requestMatchers(
                    "/api/encuestas-bienestar/**",
                    "/api/feedback-anonimo/**",
                    "/api/familiares/**"
                ).authenticated()

                // ── Todo lo demás requiere autenticación ──────────────
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}