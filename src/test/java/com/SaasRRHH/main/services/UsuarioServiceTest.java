package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.Rol;
import com.SaasRRHH.main.model.Usuario;
import com.SaasRRHH.main.repository.UsuarioRepository;
import com.SaasRRHH.main.services.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Rol rol;

    @BeforeEach
    void setUp() {
        rol = new Rol();
        rol.setIdRol(1L);
        rol.setNombreRol("ADMIN");
        rol.setDescripcion("Administrador del sistema");
    }

    @Test
    void testGuardarUsuario_EmailDuplicado() {
        // Crear un usuario NUEVO (sin ID) con email duplicado
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setId(null);  // IMPORTANTE: ID null para que sea nuevo
        nuevoUsuario.setEmail("duplicado@test.com");
        nuevoUsuario.setPassword("123456");
        nuevoUsuario.setRol(rol);
        
        // Mockear que el email YA EXISTE en la base de datos
        when(usuarioRepository.existsByEmail("duplicado@test.com")).thenReturn(true);

        // Verificar que lanza excepción
        assertThatThrownBy(() -> usuarioService.guardar(nuevoUsuario))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un usuario con el email: duplicado@test.com");
        
        // Verificar que NUNCA se llamó a save
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
    
    @Test
    void testGuardarUsuario_EmailDuplicado_ConId() {
        // Este test es para actualización con email duplicado de OTRO usuario
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(1L);
        usuarioExistente.setEmail("existente@test.com");
        usuarioExistente.setRol(rol);
        
        Usuario usuarioActualizar = new Usuario();
        usuarioActualizar.setId(2L);  // ID diferente
        usuarioActualizar.setEmail("existente@test.com");  // Mismo email que el existente
        usuarioActualizar.setRol(rol);
        
        // Mock: buscar por email encuentra el usuario con ID 1
        when(usuarioRepository.findByEmail("existente@test.com")).thenReturn(Optional.of(usuarioExistente));
        
        // Debe lanzar excepción porque el email pertenece a otro usuario
        assertThatThrownBy(() -> usuarioService.guardar(usuarioActualizar))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un usuario con el email: existente@test.com");
    }
    
    @Test
    void testGuardarUsuario_NuevoExitoso() {
        // Usuario NUEVO (sin ID)
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setId(null);
        nuevoUsuario.setEmail("nuevo@test.com");
        nuevoUsuario.setPassword("123456");
        nuevoUsuario.setRol(rol);
        
        when(usuarioRepository.existsByEmail("nuevo@test.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(nuevoUsuario);
        
        Usuario saved = usuarioService.guardar(nuevoUsuario);
        
        // Verificaciones
        verify(usuarioRepository, times(1)).existsByEmail("nuevo@test.com");
        verify(usuarioRepository, times(1)).save(nuevoUsuario);
    }
}