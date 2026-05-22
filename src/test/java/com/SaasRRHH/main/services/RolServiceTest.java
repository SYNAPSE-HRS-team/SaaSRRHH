package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.Rol;
import com.SaasRRHH.main.repository.RolRepository;
import com.SaasRRHH.main.services.impl.RolServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolServiceImpl rolService;

    private Rol rolAdmin;
    private Rol rolSupervisor;
    private Rol rolTrabajador;

    @BeforeEach
    void setUp() {
        rolAdmin = new Rol();
        rolAdmin.setIdRol(1L);
        rolAdmin.setNombreRol("ADMIN");
        rolAdmin.setDescripcion("Administrador del sistema");

        rolSupervisor = new Rol();
        rolSupervisor.setIdRol(2L);
        rolSupervisor.setNombreRol("SUPERVISOR");
        rolSupervisor.setDescripcion("Supervisor de área");

        rolTrabajador = new Rol();
        rolTrabajador.setIdRol(3L);
        rolTrabajador.setNombreRol("TRABAJADOR");
        rolTrabajador.setDescripcion("Trabajador de campo");
    }

    @Test
    void testListarRoles_Exitoso() {
        List<Rol> roles = Arrays.asList(rolAdmin, rolSupervisor, rolTrabajador);
        when(rolRepository.findAll()).thenReturn(roles);

        List<Rol> resultado = rolService.listar();

        assertThat(resultado).hasSize(3);
        assertThat(resultado.get(0).getNombreRol()).isEqualTo("ADMIN");
        verify(rolRepository, times(1)).findAll();
    }

    @Test
    void testBuscarPorId_Encontrado() {
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolAdmin));

        Optional<Rol> resultado = rolService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombreRol()).isEqualTo("ADMIN");
        verify(rolRepository, times(1)).findById(1L);
    }

    @Test
    void testBuscarPorId_NoEncontrado() {
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Rol> resultado = rolService.buscarPorId(99L);

        assertThat(resultado).isEmpty();
        verify(rolRepository, times(1)).findById(99L);
    }

    @Test
    void testGuardarRol_NuevoExitoso() {
        Rol nuevoRol = new Rol();
        nuevoRol.setNombreRol("GERENTE");
        nuevoRol.setDescripcion("Gerente general");

        when(rolRepository.existsByNombreRol("GERENTE")).thenReturn(false);
        when(rolRepository.save(any(Rol.class))).thenReturn(nuevoRol);

        Rol resultado = rolService.guardar(nuevoRol);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombreRol()).isEqualTo("GERENTE");
        verify(rolRepository, times(1)).existsByNombreRol("GERENTE");
        verify(rolRepository, times(1)).save(nuevoRol);
    }

    @Test
    void testGuardarRol_NombreDuplicado_LanzaExcepcion() {
        Rol rolDuplicado = new Rol();
        rolDuplicado.setNombreRol("ADMIN");

        when(rolRepository.existsByNombreRol("ADMIN")).thenReturn(true);

        assertThatThrownBy(() -> rolService.guardar(rolDuplicado))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un rol");
        
        verify(rolRepository, times(1)).existsByNombreRol("ADMIN");
        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void testActualizarRol_Exitoso() {
        Rol rolActualizado = new Rol();
        rolActualizado.setIdRol(1L);
        rolActualizado.setNombreRol("ADMIN");
        rolActualizado.setDescripcion("Administrador - Actualizado");

        when(rolRepository.findByNombreRol("ADMIN")).thenReturn(Optional.of(rolAdmin));
        when(rolRepository.save(any(Rol.class))).thenReturn(rolActualizado);

        Rol resultado = rolService.guardar(rolActualizado);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getDescripcion()).isEqualTo("Administrador - Actualizado");
        verify(rolRepository, times(1)).save(any(Rol.class));
    }

    @Test
    void testEliminarRol_Exitoso() {
        when(rolRepository.existsById(1L)).thenReturn(true);
        doNothing().when(rolRepository).deleteById(1L);

        rolService.eliminar(1L);

        verify(rolRepository, times(1)).existsById(1L);
        verify(rolRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarRol_NoExistente_LanzaExcepcion() {
        when(rolRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> rolService.eliminar(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Rol no encontrado");
        
        verify(rolRepository, times(1)).existsById(99L);
        verify(rolRepository, never()).deleteById(anyLong());
    }

    @Test
    void testBuscarPorNombre_Encontrado() {
        when(rolRepository.findByNombreRol("ADMIN")).thenReturn(Optional.of(rolAdmin));

        Optional<Rol> resultado = rolService.buscarPorNombre("ADMIN");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombreRol()).isEqualTo("ADMIN");
        verify(rolRepository, times(1)).findByNombreRol("ADMIN");
    }

    @Test
    void testBuscarPorNombre_NoEncontrado() {
        when(rolRepository.findByNombreRol("INEXISTENTE")).thenReturn(Optional.empty());

        Optional<Rol> resultado = rolService.buscarPorNombre("INEXISTENTE");

        assertThat(resultado).isEmpty();
        verify(rolRepository, times(1)).findByNombreRol("INEXISTENTE");
    }
}