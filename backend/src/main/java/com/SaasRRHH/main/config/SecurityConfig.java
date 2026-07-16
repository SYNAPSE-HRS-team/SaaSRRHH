package com.SaasRRHH.main.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.SaasRRHH.main.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:4200",
                "http://127.0.0.1:4200",
                "http://192.168.*:*",
                "http://10.*:*",
                "http://172.*:*",
                "https://saa-srrhh-*.vercel.app", // <-- Acepta cualquier subdominio tuyo de Vercel
                "https://saa-srrhh-iusoacbuo-saasrrhh.vercel.app" // <-- Tu url exacta actual
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Agregamos "Authorization" para que pasen tus Tokens JWT sin problemas
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Públicas
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()

                        // Perfil de usuario (cualquier autenticado)
                        .requestMatchers("/api/usuarios/profile").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/*/profile").authenticated()

                        // Cualquier usuario autenticado
                        .requestMatchers("/api/empleados/usuario/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/empleados/activos").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/empleados/dni/**").authenticated()
                        .requestMatchers("/api/tareas-asignadas/**").authenticated()
                        .requestMatchers("/api/asistencias/**").authenticated()
                        .requestMatchers("/api/feedback-anonimo/**").authenticated()
                        .requestMatchers("/api/feedback/**").authenticated()
                        .requestMatchers("/api/reportes-diarios/**").authenticated()
                        .requestMatchers("/api/boletas_pago/mis-boletas").authenticated()
                        .requestMatchers("/api/analitica/dashboard").authenticated()

                        // ADMIN y SUPERVISOR
                        .requestMatchers("/api/encuestas-bienestar/**").hasAnyRole("ADMIN", "SUPERVISOR")
                        .requestMatchers("/api/empleados/**").hasAnyRole("ADMIN", "SUPERVISOR")
                        .requestMatchers("/api/burnout/**").hasAnyRole("ADMIN", "SUPERVISOR")
                        .requestMatchers("/api/nomina/**").hasAnyRole("ADMIN", "SUPERVISOR")
                        .requestMatchers("/api/planillas/**").hasAnyRole("ADMIN", "SUPERVISOR")
                        .requestMatchers("/api/documentos-privados/**").hasAnyRole("ADMIN", "SUPERVISOR")
                        .requestMatchers("/api/tipos-documento/**").hasAnyRole("ADMIN", "SUPERVISOR")
                        .requestMatchers("/api/areas-trabajo/**").hasAnyRole("ADMIN", "SUPERVISOR")
                        .requestMatchers("/api/reportes-incidentes/**").hasAnyRole("ADMIN", "SUPERVISOR")
                        .requestMatchers("/api/boletas_pago/**").hasAnyRole("ADMIN", "SUPERVISOR")

                        // ADMIN exclusivo (VA DESPUÉS de las rutas específicas)
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers("/api/roles/**").hasRole("ADMIN")
                        // .requestMatchers("/api/dispositivos-autorizados/**").hasRole("ADMIN")
                        .requestMatchers("/api/accesos/**").hasRole("ADMIN")
                        .requestMatchers("/api/validaciones-seguridad/**").hasRole("ADMIN")

                        // Cualquier otra
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
