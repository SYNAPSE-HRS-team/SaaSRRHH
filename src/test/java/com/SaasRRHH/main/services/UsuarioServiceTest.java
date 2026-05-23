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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
        nuevoUsuario.setId(null); // IMPORTANTE: ID null para que sea nuevo
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
        usuarioActualizar.setId(2L); // ID diferente
        usuarioActualizar.setEmail("existente@test.com"); // Mismo email que el existente
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

    @Test
    void testBuscarPorId_Encontrado() {
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(1L);
        usuarioExistente.setEmail("buscarporid@test.com");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));

        Optional<Usuario> resultado = usuarioService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEmail()).isEqualTo("buscarporid@test.com");
    }

    @Test
    void testListarUsuarios_Exitoso() {
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setEmail("user1@test.com");
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setEmail("user2@test.com");

        when(usuarioRepository.findAll()).thenReturn(List.of(usuario1, usuario2));

        List<Usuario> resultado = usuarioService.listar();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(1).getEmail()).isEqualTo("user2@test.com");
        verify(usuarioRepository).findAll();
    }

    @Test
    void testEliminarUsuario_Exitoso() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        usuarioService.eliminar(1L);

        verify(usuarioRepository).existsById(1L);
        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void testBuscarPorEmail_Encontrado() {
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(1L);
        usuarioExistente.setEmail("buscar@test.com");

        when(usuarioRepository.findByEmail("buscar@test.com")).thenReturn(Optional.of(usuarioExistente));

        Optional<Usuario> resultado = usuarioService.buscarPorEmail("buscar@test.com");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEmail()).isEqualTo("buscar@test.com");
    }

    @Test
    void testActualizarUltimoAcceso_Exitoso() {
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(1L);
        usuarioExistente.setEmail("ultimo@test.com");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(usuarioExistente)).thenReturn(usuarioExistente);

        Usuario resultado = usuarioService.actualizarUltimoAcceso(1L);

        assertThat(resultado).isNotNull();
        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).save(usuarioExistente);
    }

    @Test
    void testExistsByEmail() {
        when(usuarioRepository.existsByEmail("existe@test.com")).thenReturn(true);

        boolean existe = usuarioService.existsByEmail("existe@test.com");

        assertThat(existe).isTrue();
    }
}