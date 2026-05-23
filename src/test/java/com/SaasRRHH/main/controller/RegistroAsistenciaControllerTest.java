package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.RegistroAsistencia;
import com.SaasRRHH.main.services.RegistroAsistenciaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistroAsistenciaController.class)
class RegistroAsistenciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistroAsistenciaService registroService;

    @Autowired
    private ObjectMapper objectMapper;

    private Empleado empleado;
    private RegistroAsistencia registroEntrada;
    private RegistroAsistencia registroSalida;

    @BeforeEach
    void setUp() {
        empleado = new Empleado();
        empleado.setId(1L);
        empleado.setNombres("Juan");
        empleado.setApellidos("Perez");

        registroEntrada = new RegistroAsistencia();
        registroEntrada.setId(1L);
        registroEntrada.setEmpleado(empleado);
        registroEntrada.setTipoMarcacion("ENTRADA");
        registroEntrada.setMetodo("QR");
        registroEntrada.setEstado("VALIDADO");
        registroEntrada.setFechaHora(LocalDateTime.now());

        registroSalida = new RegistroAsistencia();
        registroSalida.setId(2L);
        registroSalida.setEmpleado(empleado);
        registroSalida.setTipoMarcacion("SALIDA");
        registroSalida.setMetodo("QR");
        registroSalida.setEstado("VALIDADO");
        registroSalida.setFechaHora(LocalDateTime.now());
    }

    @Test
    void testListarRegistros_Exitoso() throws Exception {
        when(registroService.listar()).thenReturn(Arrays.asList(registroEntrada, registroSalida));

        mockMvc.perform(get("/api/registros-asistencia")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].tipoMarcacion").value("ENTRADA"))
                .andExpect(jsonPath("$[1].tipoMarcacion").value("SALIDA"));

        verify(registroService, times(1)).listar();
    }

    @Test
    void testObtenerRegistroPorId_Encontrado() throws Exception {
        when(registroService.buscarPorId(1L)).thenReturn(Optional.of(registroEntrada));

        mockMvc.perform(get("/api/registros-asistencia/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipoMarcacion").value("ENTRADA"));

        verify(registroService, times(1)).buscarPorId(1L);
    }

    @Test
    void testObtenerRegistroPorId_NoEncontrado() throws Exception {
        when(registroService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/registros-asistencia/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(registroService, times(1)).buscarPorId(99L);
    }

    @Test
    void testCrearRegistro_Exitoso() throws Exception {
        when(registroService.guardar(any(RegistroAsistencia.class))).thenReturn(registroEntrada);

        mockMvc.perform(post("/api/registros-asistencia")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registroEntrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoMarcacion").value("ENTRADA"));

        verify(registroService, times(1)).guardar(any(RegistroAsistencia.class));
    }

    @Test
    void testRegistrarEntrada_Exitoso() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("empleadoId", 1);
        payload.put("metodo", "QR");

        when(registroService.registrarEntrada(1L, "QR")).thenReturn(registroEntrada);

        mockMvc.perform(post("/api/registros-asistencia/entrada")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoMarcacion").value("ENTRADA"));

        verify(registroService, times(1)).registrarEntrada(1L, "QR");
    }

    @Test
    void testRegistrarSalida_Exitoso() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("empleadoId", 1);
        payload.put("metodo", "QR");

        when(registroService.registrarSalida(1L, "QR")).thenReturn(registroSalida);

        mockMvc.perform(post("/api/registros-asistencia/salida")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoMarcacion").value("SALIDA"));

        verify(registroService, times(1)).registrarSalida(1L, "QR");
    }

    @Test
    void testBuscarPorEmpleado() throws Exception {
        when(registroService.buscarPorEmpleado(1L)).thenReturn(Arrays.asList(registroEntrada, registroSalida));

        mockMvc.perform(get("/api/registros-asistencia/empleado/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(registroService, times(1)).buscarPorEmpleado(1L);
    }

    @Test
    void testEliminarRegistro_Exitoso() throws Exception {
        doNothing().when(registroService).eliminar(1L);

        mockMvc.perform(delete("/api/registros-asistencia/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(registroService, times(1)).eliminar(1L);
    }

    @Test
    void testEliminarRegistro_NoEncontrado_RetornaNotFound() throws Exception {
        doThrow(new RuntimeException("Registro no encontrado")).when(registroService).eliminar(99L);

        mockMvc.perform(delete("/api/registros-asistencia/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(registroService, times(1)).eliminar(99L);
    }
}