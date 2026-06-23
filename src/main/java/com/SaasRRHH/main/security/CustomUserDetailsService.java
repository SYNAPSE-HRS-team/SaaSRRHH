package com.SaasRRHH.main.security;

import com.SaasRRHH.main.model.Usuario;
import com.SaasRRHH.main.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Carga el usuario desde la tabla `usuarios` de PostgreSQL.
 * El "username" que usa Spring Security es el email del usuario.
 * El rol se toma de la relación usuario -> rol -> nombre_rol,
 * y se prefija con "ROLE_" para que Spring Security lo reconozca.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con email: " + email));

        if (!usuario.getActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + email);
        }

        // nombre_rol en BD: ej. "ADMIN", "SUPERVISOR", "EMPLEADO"
        // Spring Security espera "ROLE_ADMIN", "ROLE_SUPERVISOR", etc.
        String roleAuthority = "ROLE_" + usuario.getRol().getNombreRol().toUpperCase();

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(roleAuthority)))
                .build();
    }
}
