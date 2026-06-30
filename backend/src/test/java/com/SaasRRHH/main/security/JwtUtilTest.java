package com.SaasRRHH.main.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test unitario de JwtUtil.
 * No levanta el contexto de Spring — va muy rápido.
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;

    // Clave Base64 de 32 bytes válida para tests
    private static final String SECRET =
            "dGVzdFNlY3JldEtleVBhcmFKd3RVbml0VGVzdHMxMjM=";
    private static final long EXPIRATION = 3_600_000L; // 1 hora

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", EXPIRATION);
    }

    // =========================================================
    // generateToken
    // =========================================================

    @Test
    @DisplayName("generateToken devuelve un string no vacío")
    void generateToken_devuelveStringNoVacio() {
        UserDetails user = crearUser("admin@test.com", "ROLE_ADMIN");

        String token = jwtUtil.generateToken(user);

        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("generateToken produce un JWT con 3 partes separadas por punto")
    void generateToken_tieneFormatoJWT() {
        UserDetails user = crearUser("user@test.com", "ROLE_EMPLEADO");

        String token = jwtUtil.generateToken(user);
        String[] partes = token.split("\\.");

        assertThat(partes).hasSize(3);
    }

    // =========================================================
    // extractUsername
    // =========================================================

    @Test
    @DisplayName("extractUsername devuelve el email correcto del token")
    void extractUsername_devuelveEmailCorrecto() {
        UserDetails user = crearUser("supervisor@empresa.com", "ROLE_SUPERVISOR");
        String token = jwtUtil.generateToken(user);

        String username = jwtUtil.extractUsername(token);

        assertThat(username).isEqualTo("supervisor@empresa.com");
    }

    // =========================================================
    // isTokenValid
    // =========================================================

    @Test
    @DisplayName("isTokenValid retorna true para token recién generado del mismo usuario")
    void isTokenValid_tokenFresco_retornaTrue() {
        UserDetails user = crearUser("admin@test.com", "ROLE_ADMIN");
        String token = jwtUtil.generateToken(user);

        boolean valido = jwtUtil.isTokenValid(token, user);

        assertThat(valido).isTrue();
    }

    @Test
    @DisplayName("isTokenValid retorna false si el username no coincide")
    void isTokenValid_usuarioDistinto_retornaFalse() {
        UserDetails userA = crearUser("userA@test.com", "ROLE_ADMIN");
        UserDetails userB = crearUser("userB@test.com", "ROLE_ADMIN");
        String token = jwtUtil.generateToken(userA);

        boolean valido = jwtUtil.isTokenValid(token, userB);

        assertThat(valido).isFalse();
    }

    @Test
    @DisplayName("isTokenValid retorna false para token expirado")
    void isTokenValid_tokenExpirado_retornaFalse() throws Exception {
        // Generar un token que ya expiró (expiration = -1 ms)
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1L);
        UserDetails user = crearUser("admin@test.com", "ROLE_ADMIN");
        String token = jwtUtil.generateToken(user);

        // Restaurar expiración normal para que el parser no falle
        ReflectionTestUtils.setField(jwtUtil, "expiration", EXPIRATION);

        // Token expirado → isTokenValid debe retornar false o lanzar excepción
        // (JJWT lanza ExpiredJwtException al parsear)
        assertThatThrownBy(() -> jwtUtil.isTokenValid(token, user))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("extractUsername lanza excepción para token completamente inválido")
    void extractUsername_tokenInvalido_lanzaExcepcion() {
        assertThatThrownBy(() -> jwtUtil.extractUsername("esto.no.es.un.jwt"))
                .isInstanceOf(Exception.class);
    }

    // =========================================================
    // Helper
    // =========================================================

    private UserDetails crearUser(String email, String rol) {
        return User.builder()
                .username(email)
                .password("hashedPassword")
                .authorities(List.of(new SimpleGrantedAuthority(rol)))
                .build();
    }
}
