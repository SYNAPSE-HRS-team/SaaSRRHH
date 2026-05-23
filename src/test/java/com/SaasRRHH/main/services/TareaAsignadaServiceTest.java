package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.*;
import com.SaasRRHH.main.repository.TareaAsignadaRepository;
import com.SaasRRHH.main.services.impl.TareaAsignadaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TareaAsignadaServiceTest {

    @Mock
    private TareaAsignadaRepository tareaRepository;

    @Mock
    private EmpleadoService empleadoService;

    @Mock
    private AreaTrabajoService areaService;

    @InjectMocks
    private TareaAsignadaServiceImpl tareaService;

    private Empleado empleado;
    private Empleado supervisor;
    private AreaTrabajo area;
    private TareaAsignada tarea;

    @BeforeEach
    void setUp() {
        empleado = new Empleado();
        empleado.setId(1L);
        empleado.setNombres("Juan");
        empleado.setApellidos("Perez");

        supervisor = new Empleado();
        supervisor.setId(2L);
        supervisor.setNombres("Carlos");
        supervisor.setApellidos("Lopez");

        area = new AreaTrabajo();
        area.setId(1L);
        area.setNombre("Parcela Norte");
        area.setCultivoTipo("Papa");

        tarea = new TareaAsignada();
        tarea.setId(1L);
        tarea.setEmpleado(empleado);
        tarea.setSupervisor(supervisor);
        tarea.setArea(area);
        tarea.setFuncion(TareaAsignada.Funcion.CULTIVADOR);
        tarea.setFecha(LocalDate.now());
        tarea.setDescripcion("Preparar terreno");
        tarea.setEstado(TareaAsignada.EstadoTarea.PENDIENTE);
    }

    @Test
    void testListarTareas_Exitoso() {
        when(tareaRepository.findAll()).thenReturn(Arrays.asList(tarea));

        List<TareaAsignada> resultado = tareaService.listar();

        assertThat(resultado).hasSize(1);
        verify(tareaRepository, times(1)).findAll();
    }

    @Test
    void testBuscarPorId_Encontrado() {
        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));

        Optional<TareaAsignada> resultado = tareaService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getFuncion()).isEqualTo(TareaAsignada.Funcion.CULTIVADOR);
        verify(tareaRepository, times(1)).findById(1L);
    }

    @Test
    void testGuardarTarea_Exitoso() {
        when(empleadoService.buscarPorId(1L)).thenReturn(Optional.of(empleado));
        when(empleadoService.buscarPorId(2L)).thenReturn(Optional.of(supervisor));
        when(areaService.buscarPorId(1L)).thenReturn(Optional.of(area));
        when(tareaRepository.save(any(TareaAsignada.class))).thenReturn(tarea);

        TareaAsignada resultado = tareaService.guardar(tarea);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getFuncion()).isEqualTo(TareaAsignada.Funcion.CULTIVADOR);
        verify(tareaRepository, times(1)).save(tarea);
    }

    @Test
    void testGuardarTarea_EmpleadoNoExistente_LanzaExcepcion() {
        when(empleadoService.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tareaService.guardar(tarea))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Empleado no encontrado");
        
        verify(tareaRepository, never()).save(any());
    }

    @Test
    void testCambiarEstado_Exitoso() {
        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));
        when(tareaRepository.save(any(TareaAsignada.class))).thenReturn(tarea);

        TareaAsignada resultado = tareaService.cambiarEstado(1L, TareaAsignada.EstadoTarea.EN_PROGRESO);

        assertThat(resultado.getEstado()).isEqualTo(TareaAsignada.EstadoTarea.EN_PROGRESO);
        verify(tareaRepository, times(1)).save(tarea);
    }

    @Test
    void testEliminarTarea_Exitoso() {
        when(tareaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(tareaRepository).deleteById(1L);

        tareaService.eliminar(1L);

        verify(tareaRepository, times(1)).deleteById(1L);
    }
}