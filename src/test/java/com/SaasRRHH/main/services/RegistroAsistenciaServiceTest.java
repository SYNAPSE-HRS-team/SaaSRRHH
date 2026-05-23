package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.RegistroAsistencia;
import com.SaasRRHH.main.model.Rol;
import com.SaasRRHH.main.model.Usuario;
import com.SaasRRHH.main.repository.RegistroAsistenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistroAsistenciaServiceTest {

    @Mock
    private RegistroAsistenciaRepository registroRepository;

    @Mock
    private EmpleadoService empleadoService;

    @InjectMocks
    private RegistroAsistenciaService registroService;

    private Empleado empleado;
    private RegistroAsistencia registroEntrada;
    private RegistroAsistencia registroSalida;

    @BeforeEach
    void setUp() {
        // Crear empleado
        empleado = new Empleado();
        empleado.setId(1L);
        empleado.setNombres("Juan");
        empleado.setApellidos("Perez");
        empleado.setDni("12345678");

        // Crear registro de entrada
        registroEntrada = new RegistroAsistencia();
        registroEntrada.setId(1L);
        registroEntrada.setEmpleado(empleado);
        registroEntrada.setTipoMarcacion("ENTRADA");
        registroEntrada.setMetodo("QR");
        registroEntrada.setEstado("VALIDADO");
        registroEntrada.setFechaHora(LocalDateTime.now());

        // Crear registro de salida
        registroSalida = new RegistroAsistencia();
        registroSalida.setId(2L);
        registroSalida.setEmpleado(empleado);
        registroSalida.setTipoMarcacion("SALIDA");
        registroSalida.setMetodo("QR");
        registroSalida.setEstado("VALIDADO");
        registroSalida.setFechaHora(LocalDateTime.now());
    }

    @Test
    void testListarRegistros_Exitoso() {
        when(registroRepository.findAll()).thenReturn(Arrays.asList(registroEntrada, registroSalida));

        List<RegistroAsistencia> resultado = registroService.listar();

        assertThat(resultado).hasSize(2);
        verify(registroRepository, times(1)).findAll();
    }

    @Test
    void testBuscarPorId_Encontrado() {
        when(registroRepository.findById(1L)).thenReturn(Optional.of(registroEntrada));

        Optional<RegistroAsistencia> resultado = registroService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getTipoMarcacion()).isEqualTo("ENTRADA");
        verify(registroRepository, times(1)).findById(1L);
    }

    @Test
    void testBuscarPorId_NoEncontrado() {
        when(registroRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<RegistroAsistencia> resultado = registroService.buscarPorId(99L);

        assertThat(resultado).isEmpty();
        verify(registroRepository, times(1)).findById(99L);
    }

    @Test
    void testGuardarRegistro_Exitoso() {
        when(empleadoService.buscarPorId(1L)).thenReturn(Optional.of(empleado));
        when(registroRepository.save(any(RegistroAsistencia.class))).thenReturn(registroEntrada);

        RegistroAsistencia resultado = registroService.guardar(registroEntrada);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTipoMarcacion()).isEqualTo("ENTRADA");
        verify(registroRepository, times(1)).save(registroEntrada);
    }

    @Test
    void testGuardarRegistro_EmpleadoNoExistente_LanzaExcepcion() {
        RegistroAsistencia registro = new RegistroAsistencia();
        registro.setEmpleado(empleado);
        
        when(empleadoService.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registroService.guardar(registro))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Empleado no encontrado");
        
        verify(registroRepository, never()).save(any());
    }

    @Test
    void testGuardarRegistro_TipoMarcacionInvalido_LanzaExcepcion() {
        when(empleadoService.buscarPorId(1L)).thenReturn(Optional.of(empleado));
        
        RegistroAsistencia registroInvalido = new RegistroAsistencia();
        registroInvalido.setEmpleado(empleado);
        registroInvalido.setTipoMarcacion("INVALIDO");

        assertThatThrownBy(() -> registroService.guardar(registroInvalido))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tipo de marcación inválido");
        
        verify(registroRepository, never()).save(any());
    }

    @Test
    void testRegistrarEntrada_Exitoso() {
        when(empleadoService.buscarPorId(1L)).thenReturn(Optional.of(empleado));
        when(registroRepository.save(any(RegistroAsistencia.class))).thenReturn(registroEntrada);

        RegistroAsistencia resultado = registroService.registrarEntrada(1L, "QR");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTipoMarcacion()).isEqualTo("ENTRADA");
        verify(registroRepository, times(1)).save(any(RegistroAsistencia.class));
    }

    @Test
    void testRegistrarSalida_Exitoso() {
        when(empleadoService.buscarPorId(1L)).thenReturn(Optional.of(empleado));
        when(registroRepository.save(any(RegistroAsistencia.class))).thenReturn(registroSalida);

        RegistroAsistencia resultado = registroService.registrarSalida(1L, "QR");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTipoMarcacion()).isEqualTo("SALIDA");
        verify(registroRepository, times(1)).save(any(RegistroAsistencia.class));
    }

    @Test
    void testEliminarRegistro_Exitoso() {
        when(registroRepository.existsById(1L)).thenReturn(true);
        doNothing().when(registroRepository).deleteById(1L);

        registroService.eliminar(1L);

        verify(registroRepository, times(1)).existsById(1L);
        verify(registroRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarRegistro_NoExistente_LanzaExcepcion() {
        when(registroRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> registroService.eliminar(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Registro de asistencia no encontrado");
        
        verify(registroRepository, never()).deleteById(anyLong());
    }

    @Test
    void testBuscarPorEmpleado() {
        when(registroRepository.findByEmpleadoId(1L)).thenReturn(Arrays.asList(registroEntrada, registroSalida));

        List<RegistroAsistencia> resultado = registroService.buscarPorEmpleado(1L);

        assertThat(resultado).hasSize(2);
        verify(registroRepository, times(1)).findByEmpleadoId(1L);
    }
}