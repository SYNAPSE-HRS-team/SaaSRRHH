package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para el flujo completo de autenticación JWT.
 *
 * REQUISITOS PREVIOS:
 * - Base de datos de test activa (ver application-test.properties)
 * - Usuario con email "admin@test.com", password BCrypt de "admin123", rol ADMIN
 * - Usuario con email "supervisor@test.com", password BCrypt de "sup123", rol SUPERVISOR
 * - Usuario con email "empleado@test.com", password BCrypt de "emp123", rol EMPLEADO
 *
 * Cómo insertar los usuarios de prueba en tu BD:
 * -- Insertar rol ADMIN (si no existe)
 * INSERT INTO roles (nombre_rol, descripcion) VALUES ('ADMIN', 'Administrador') ON CONFLICT DO NOTHING;
 * INSERT INTO roles (nombre_rol, descripcion) VALUES ('SUPERVISOR', 'Supervisor') ON CONFLICT DO NOTHING;
 * INSERT INTO roles (nombre_rol, descripcion) VALUES ('EMPLEADO', 'Empleado') ON CONFLICT DO NOTHING;
 *
 * -- Contraseña BCrypt de "admin123"
 * INSERT INTO usuarios (email, password, rol_id, activo)
 * VALUES ('admin@test.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 1, true);
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    // =========================================================
    // LOGIN - casos exitosos
    // =========================================================

    @Test
    @DisplayName("Login exitoso con credenciales válidas de ADMIN → devuelve token")
    void login_conCredencialesValidas_devuelveToken() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "admin@test.com",
                            "password": "admin123"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("admin@test.com"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("Login exitoso con credenciales válidas de SUPERVISOR")
    void login_conCredencialesSupervisor_devuelveToken() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "supervisor@test.com",
                            "password": "sup123"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.roles[0]").value("ROLE_SUPERVISOR"));
    }

    // =========================================================
    // LOGIN - casos fallidos
    // =========================================================

    @Test
    @DisplayName("Login con password incorrecto → 401")
    void login_conPasswordIncorrecto_retorna401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "admin@test.com",
                            "password": "passwordMAL"
                        }
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales inválidas"));
    }

    @Test
    @DisplayName("Login con email inexistente → 401")
    void login_conEmailInexistente_retorna401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "noexiste@test.com",
                            "password": "cualquiera"
                        }
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Login con body vacío → 401 o 400")
    void login_conBodyVacio_retornaError() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().is4xxClientError());
    }

    // =========================================================
    // ACCESO CON TOKEN VÁLIDO
    // =========================================================

    @Test
    @DisplayName("GET /api/empleados con token ADMIN válido → 200")
    void accesoConTokenAdmin_aRutaProtegida_retorna200() throws Exception {
        String token = obtenerTokenAdmin();

        mockMvc.perform(get("/api/empleados")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/empleados con token SUPERVISOR válido → 200")
    void accesoConTokenSupervisor_aRutaPermitida_retorna200() throws Exception {
        String token = obtenerTokenSupervisor();

        mockMvc.perform(get("/api/empleados")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    // =========================================================
    // ACCESO DENEGADO POR ROL
    // =========================================================

    @Test
    @DisplayName("GET /api/usuarios con token SUPERVISOR → 403 (solo ADMIN)")
    void accesoConTokenSupervisor_aRutaSoloAdmin_retorna403() throws Exception {
        String token = obtenerTokenSupervisor();

        mockMvc.perform(get("/api/usuarios")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/nomina/planillas con token SUPERVISOR → 403")
    void accesoConTokenSupervisor_aNomina_retorna403() throws Exception {
        String token = obtenerTokenSupervisor();

        mockMvc.perform(get("/api/planillas")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    // =========================================================
    // ACCESO SIN TOKEN
    // =========================================================

    @Test
    @DisplayName("GET /api/empleados sin token → 403")
    void accesoSinToken_aRutaProtegida_retorna403() throws Exception {
        mockMvc.perform(get("/api/empleados"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/analitica/dashboard sin token → 403")
    void accesoSinToken_aDashboard_retorna403() throws Exception {
        mockMvc.perform(get("/api/analitica/dashboard"))
                .andExpect(status().isForbidden());
    }

    // =========================================================
    // ACCESO CON TOKEN INVÁLIDO / MALFORMADO
    // =========================================================

    @Test
    @DisplayName("GET /api/empleados con token falso → 403")
    void accesoConTokenFalso_retorna403() throws Exception {
        mockMvc.perform(get("/api/empleados")
                .header("Authorization", "Bearer tokenFalsoQueNoEsJWT"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/empleados con header Authorization malformado → 403")
    void accesoConHeaderMalformado_retorna403() throws Exception {
        mockMvc.perform(get("/api/empleados")
                .header("Authorization", "SinBearer tokenFalso"))
                .andExpect(status().isForbidden());
    }

    // =========================================================
    // RUTA PÚBLICA
    // =========================================================

    @Test
    @DisplayName("POST /api/auth/login es pública (no requiere token)")
    void loginEsRutaPublica() throws Exception {
        // Solo verificamos que no devuelva 401 por falta de token
        // (puede devolver 401 por credenciales malas, lo cual es correcto)
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "admin@test.com",
                            "password": "admin123"
                        }
                        """))
                .andExpect(status().isOk());
    }

    // =========================================================
    // HELPERS
    // =========================================================

    /**
     * Hace login real y extrae el token de la respuesta.
     * Alternativa: generar el token directamente via JwtUtil
     * para evitar depender del endpoint en otros tests.
     */
    private String obtenerTokenAdmin() {
        var userDetails = userDetailsService.loadUserByUsername("admin@test.com");
        return jwtUtil.generateToken(userDetails);
    }

    private String obtenerTokenSupervisor() {
        var userDetails = userDetailsService.loadUserByUsername("supervisor@test.com");
        return jwtUtil.generateToken(userDetails);
    }

    private String obtenerTokenEmpleado() {
        var userDetails = userDetailsService.loadUserByUsername("empleado@test.com");
        return jwtUtil.generateToken(userDetails);
    }
}
