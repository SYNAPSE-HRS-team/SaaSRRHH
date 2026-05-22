package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.*;
import com.SaasRRHH.main.services.TareaAsignadaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TareaAsignadaController.class)
class TareaAsignadaControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TareaAsignadaService tareaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Empleado empleado;
    private Empleado supervisor;
    private AreaTrabajo area;
    private TareaAsignada tarea1;
    private TareaAsignada tarea2;

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

        tarea1 = new TareaAsignada();
        tarea1.setId(1L);
        tarea1.setEmpleado(empleado);
        tarea1.setSupervisor(supervisor);
        tarea1.setArea(area);
        tarea1.setFuncion(TareaAsignada.Funcion.CULTIVADOR);
        tarea1.setFecha(LocalDate.of(2024, 1, 15));
        tarea1.setDescripcion("Preparar terreno");
        tarea1.setEstado(TareaAsignada.EstadoTarea.PENDIENTE);

        tarea2 = new TareaAsignada();
        tarea2.setId(2L);
        tarea2.setEmpleado(empleado);
        tarea2.setSupervisor(supervisor);
        tarea2.setArea(area);
        tarea2.setFuncion(TareaAsignada.Funcion.ROCIADOR);
        tarea2.setFecha(LocalDate.of(2024, 1, 16));
        tarea2.setDescripcion("Aplicar fertilizante");
        tarea2.setEstado(TareaAsignada.EstadoTarea.EN_PROGRESO);
    }

    // ========== TEST GET ALL ==========
    @Test
    void testListarTareas_Exitoso() throws Exception {
        when(tareaService.listar()).thenReturn(Arrays.asList(tarea1, tarea2));

        mockMvc.perform(get("/api/tareas-asignadas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].funcion").value("CULTIVADOR"))
                .andExpect(jsonPath("$[1].funcion").value("ROCIADOR"));

        verify(tareaService, times(1)).listar();
    }

    @Test
    void testListarTareas_Vacio() throws Exception {
        when(tareaService.listar()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/tareas-asignadas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(tareaService, times(1)).listar();
    }

    // ========== TEST GET BY ID ==========
    @Test
    void testObtenerTareaPorId_Encontrado() throws Exception {
        when(tareaService.buscarPorId(1L)).thenReturn(Optional.of(tarea1));

        mockMvc.perform(get("/api/tareas-asignadas/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.funcion").value("CULTIVADOR"))
                .andExpect(jsonPath("$.descripcion").value("Preparar terreno"));

        verify(tareaService, times(1)).buscarPorId(1L);
    }

    @Test
    void testObtenerTareaPorId_NoEncontrado() throws Exception {
        when(tareaService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tareas-asignadas/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(tareaService, times(1)).buscarPorId(99L);
    }

    // ========== TEST POST CREATE ==========
    @Test
    void testCrearTarea_Exitoso() throws Exception {
        TareaAsignada nuevaTarea = new TareaAsignada();
        nuevaTarea.setEmpleado(empleado);
        nuevaTarea.setSupervisor(supervisor);
        nuevaTarea.setArea(area);
        nuevaTarea.setFuncion(TareaAsignada.Funcion.ARADOR);
        nuevaTarea.setFecha(LocalDate.of(2024, 1, 17));
        nuevaTarea.setDescripcion("Arar la tierra");
        nuevaTarea.setEstado(TareaAsignada.EstadoTarea.PENDIENTE);

        TareaAsignada tareaCreada = new TareaAsignada();
        tareaCreada.setId(3L);
        tareaCreada.setEmpleado(empleado);
        tareaCreada.setSupervisor(supervisor);
        tareaCreada.setArea(area);
        tareaCreada.setFuncion(TareaAsignada.Funcion.ARADOR);
        tareaCreada.setFecha(LocalDate.of(2024, 1, 17));
        tareaCreada.setDescripcion("Arar la tierra");
        tareaCreada.setEstado(TareaAsignada.EstadoTarea.PENDIENTE);

        when(tareaService.guardar(any(TareaAsignada.class))).thenReturn(tareaCreada);

        mockMvc.perform(post("/api/tareas-asignadas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevaTarea)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.funcion").value("ARADOR"));

        verify(tareaService, times(1)).guardar(any(TareaAsignada.class));
    }

    @Test
    void testCrearTarea_EmpleadoNoExistente_RetornaBadRequest() throws Exception {
        TareaAsignada tareaInvalida = new TareaAsignada();
        tareaInvalida.setEmpleado(empleado);
        tareaInvalida.setSupervisor(supervisor);
        tareaInvalida.setArea(area);

        when(tareaService.guardar(any(TareaAsignada.class)))
            .thenThrow(new RuntimeException("Empleado no encontrado"));

        mockMvc.perform(post("/api/tareas-asignadas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tareaInvalida)))
                .andExpect(status().isBadRequest());

        verify(tareaService, times(1)).guardar(any(TareaAsignada.class));
    }

    // ========== TEST PUT UPDATE ==========
    @Test
    void testActualizarTarea_Exitoso() throws Exception {
        TareaAsignada tareaActualizada = new TareaAsignada();
        tareaActualizada.setFuncion(TareaAsignada.Funcion.CULTIVADOR);
        tareaActualizada.setDescripcion("Preparar terreno - ACTUALIZADO");
        tareaActualizada.setEstado(TareaAsignada.EstadoTarea.COMPLETADO);

        TareaAsignada tareaActualizadaConId = new TareaAsignada();
        tareaActualizadaConId.setId(1L);
        tareaActualizadaConId.setFuncion(TareaAsignada.Funcion.CULTIVADOR);
        tareaActualizadaConId.setDescripcion("Preparar terreno - ACTUALIZADO");
        tareaActualizadaConId.setEstado(TareaAsignada.EstadoTarea.COMPLETADO);

        when(tareaService.actualizar(eq(1L), any(TareaAsignada.class)))
            .thenReturn(Optional.of(tareaActualizadaConId));

        mockMvc.perform(put("/api/tareas-asignadas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tareaActualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion").value("Preparar terreno - ACTUALIZADO"))
                .andExpect(jsonPath("$.estado").value("COMPLETADO"));

        verify(tareaService, times(1)).actualizar(eq(1L), any(TareaAsignada.class));
    }

    @Test
    void testActualizarTarea_NoEncontrada_RetornaNotFound() throws Exception {
        TareaAsignada tareaActualizada = new TareaAsignada();
        tareaActualizada.setFuncion(TareaAsignada.Funcion.CULTIVADOR);

        when(tareaService.actualizar(eq(99L), any(TareaAsignada.class)))
            .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/tareas-asignadas/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tareaActualizada)))
                .andExpect(status().isNotFound());

        verify(tareaService, times(1)).actualizar(eq(99L), any(TareaAsignada.class));
    }

    // ========== TEST DELETE ==========
    @Test
    void testEliminarTarea_Exitoso() throws Exception {
        doNothing().when(tareaService).eliminar(1L);

        mockMvc.perform(delete("/api/tareas-asignadas/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(tareaService, times(1)).eliminar(1L);
    }

    @Test
    void testEliminarTarea_NoEncontrada_RetornaNotFound() throws Exception {
        doThrow(new RuntimeException("Tarea no encontrada con id: 99")).when(tareaService).eliminar(99L);

        mockMvc.perform(delete("/api/tareas-asignadas/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(tareaService, times(1)).eliminar(99L);
    }

    // ========== TEST BUSCAR POR EMPLEADO ==========
    @Test
    void testBuscarTareasPorEmpleado_Exitoso() throws Exception {
        when(tareaService.buscarPorEmpleado(1L)).thenReturn(Arrays.asList(tarea1, tarea2));

        mockMvc.perform(get("/api/tareas-asignadas/empleado/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(tareaService, times(1)).buscarPorEmpleado(1L);
    }

    @Test
    void testBuscarTareasPorEmpleado_Vacio() throws Exception {
        when(tareaService.buscarPorEmpleado(1L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/tareas-asignadas/empleado/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(tareaService, times(1)).buscarPorEmpleado(1L);
    }

    // ========== TEST BUSCAR POR SUPERVISOR ==========
    @Test
    void testBuscarTareasPorSupervisor_Exitoso() throws Exception {
        when(tareaService.buscarPorSupervisor(2L)).thenReturn(Arrays.asList(tarea1, tarea2));

        mockMvc.perform(get("/api/tareas-asignadas/supervisor/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(tareaService, times(1)).buscarPorSupervisor(2L);
    }

    // ========== TEST BUSCAR POR ESTADO ==========
    @Test
    void testBuscarTareasPorEstado_Exitoso() throws Exception {
        when(tareaService.buscarPorEstado(TareaAsignada.EstadoTarea.PENDIENTE))
            .thenReturn(Arrays.asList(tarea1));

        mockMvc.perform(get("/api/tareas-asignadas/estado/PENDIENTE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));

        verify(tareaService, times(1)).buscarPorEstado(TareaAsignada.EstadoTarea.PENDIENTE);
    }

    @Test
    void testBuscarTareasPorEstado_Invalido_RetornaBadRequest() throws Exception {
        mockMvc.perform(get("/api/tareas-asignadas/estado/INVALIDO")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(tareaService, never()).buscarPorEstado(any());
    }

    // ========== TEST BUSCAR POR EMPLEADO Y FECHA ==========
    @Test
    void testBuscarTareasPorEmpleadoYFecha_Exitoso() throws Exception {
        when(tareaService.buscarPorEmpleadoYFecha(1L, LocalDate.of(2024, 1, 15)))
            .thenReturn(Arrays.asList(tarea1));

        mockMvc.perform(get("/api/tareas-asignadas/empleado/1/fecha")
                .param("fecha", "2024-01-15")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].fecha").value("2024-01-15"));

        verify(tareaService, times(1)).buscarPorEmpleadoYFecha(eq(1L), any(LocalDate.class));
    }

    // ========== TEST CAMBIAR ESTADO ==========
    @Test
    void testCambiarEstadoTarea_Exitoso() throws Exception {
        TareaAsignada tareaActualizada = new TareaAsignada();
        tareaActualizada.setId(1L);
        tareaActualizada.setEstado(TareaAsignada.EstadoTarea.EN_PROGRESO);

        when(tareaService.cambiarEstado(1L, TareaAsignada.EstadoTarea.EN_PROGRESO))
            .thenReturn(tareaActualizada);

        mockMvc.perform(patch("/api/tareas-asignadas/1/estado")
                .param("estado", "EN_PROGRESO")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_PROGRESO"));

        verify(tareaService, times(1)).cambiarEstado(1L, TareaAsignada.EstadoTarea.EN_PROGRESO);
    }

    @Test
    void testCambiarEstadoTarea_NoEncontrada_RetornaNotFound() throws Exception {
        when(tareaService.cambiarEstado(99L, TareaAsignada.EstadoTarea.EN_PROGRESO))
            .thenThrow(new RuntimeException("Tarea no encontrada"));

        mockMvc.perform(patch("/api/tareas-asignadas/99/estado")
                .param("estado", "EN_PROGRESO")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(tareaService, times(1)).cambiarEstado(99L, TareaAsignada.EstadoTarea.EN_PROGRESO);
    }

    @Test
    void testCambiarEstadoTarea_EstadoInvalido_RetornaBadRequest() throws Exception {
        mockMvc.perform(patch("/api/tareas-asignadas/1/estado")
                .param("estado", "INVALIDO")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(tareaService, never()).cambiarEstado(anyLong(), any());
    }
}




