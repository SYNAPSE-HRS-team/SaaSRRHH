package com.SaasRRHH.main.security;

import com.SaasRRHH.main.model.Rol;
import com.SaasRRHH.main.model.Usuario;
import com.SaasRRHH.main.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Test unitario de CustomUserDetailsService usando Mockito.
 * No levanta Spring — rápido y aislado.
 */
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    // =========================================================
    // loadUserByUsername - casos felices
    // =========================================================

    @Test
    @DisplayName("Carga correctamente un usuario ADMIN activo")
    void loadUserByUsername_usuarioAdminActivo_devuelveUserDetails() {
        Usuario admin = crearUsuario("admin@empresa.com", "hashPassword", "ADMIN", true);
        when(usuarioRepository.findByEmail("admin@empresa.com"))
                .thenReturn(Optional.of(admin));

        UserDetails result = service.loadUserByUsername("admin@empresa.com");

        assertThat(result.getUsername()).isEqualTo("admin@empresa.com");
        assertThat(result.getPassword()).isEqualTo("hashPassword");
        assertThat(result.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("Carga correctamente un usuario SUPERVISOR activo")
    void loadUserByUsername_usuarioSupervisor_tieneRolCorrecto() {
        Usuario sup = crearUsuario("sup@empresa.com", "hash", "SUPERVISOR", true);
        when(usuarioRepository.findByEmail("sup@empresa.com"))
                .thenReturn(Optional.of(sup));

        UserDetails result = service.loadUserByUsername("sup@empresa.com");

        assertThat(result.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERVISOR"));
    }

    @Test
    @DisplayName("El rol se convierte a mayúsculas correctamente")
    void loadUserByUsername_rolEnMinusculas_seConvierteAMayusculas() {
        // nombre_rol en minúsculas en BD
        Usuario user = crearUsuario("emp@empresa.com", "hash", "empleado", true);
        when(usuarioRepository.findByEmail("emp@empresa.com"))
                .thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("emp@empresa.com");

        assertThat(result.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLEADO"));
    }

    @Test
    @DisplayName("Solo tiene exactamente una autoridad (un rol)")
    void loadUserByUsername_tieneExactamenteUnRol() {
        Usuario user = crearUsuario("user@empresa.com", "hash", "ADMIN", true);
        when(usuarioRepository.findByEmail("user@empresa.com"))
                .thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("user@empresa.com");

        assertThat(result.getAuthorities()).hasSize(1);
    }

    // =========================================================
    // loadUserByUsername - casos de error
    // =========================================================

    @Test
    @DisplayName("Email no encontrado → lanza UsernameNotFoundException")
    void loadUserByUsername_emailNoExiste_lanzaExcepcion() {
        when(usuarioRepository.findByEmail("noexiste@empresa.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("noexiste@empresa.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("noexiste@empresa.com");
    }

    @Test
    @DisplayName("Usuario inactivo → lanza UsernameNotFoundException")
    void loadUserByUsername_usuarioInactivo_lanzaExcepcion() {
        Usuario inactivo = crearUsuario("inactivo@empresa.com", "hash", "ADMIN", false);
        when(usuarioRepository.findByEmail("inactivo@empresa.com"))
                .thenReturn(Optional.of(inactivo));

        assertThatThrownBy(() -> service.loadUserByUsername("inactivo@empresa.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("inactivo");
    }

    // =========================================================
    // Helper
    // =========================================================

    private Usuario crearUsuario(String email, String password,
                                  String nombreRol, boolean activo) {
        Rol rol = new Rol();
        rol.setIdRol(1L);
        rol.setNombreRol(nombreRol);

        Usuario u = new Usuario();
        u.setId(1L);
        u.setEmail(email);
        u.setPassword(password);
        u.setRol(rol);
        u.setActivo(activo);
        return u;
    }
}
