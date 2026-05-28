package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.MetricaBurnout;
import com.SaasRRHH.main.security.JwtUtil;
import com.SaasRRHH.main.services.MetricaBurnoutService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MetricaBurnoutController.class)
@AutoConfigureMockMvc(addFilters = false)

class MetricaBurnoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MetricaBurnoutService metricaBurnoutService;
    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private MetricaBurnout metrica;

    @BeforeEach
    void setUp() {
        metrica = new MetricaBurnout();
        metrica.setId(1L);
        metrica.setNivelRiesgo(MetricaBurnout.NivelRiesgoBurnout.MEDIO);
        metrica.setHorasExtraAcumuladas(10);
        metrica.setTendenciaTardanza(false);
        metrica.setFechaEvaluacion(LocalDateTime.now());
    }

    // ===================== GET ALL =====================

    @Test
    void listarMetricas_debeRetornarLista() throws Exception {
        List<MetricaBurnout> metricas = Arrays.asList(metrica);
        when(metricaBurnoutService.listar()).thenReturn(metricas);

        mockMvc.perform(get("/api/burnout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));

        verify(metricaBurnoutService).listar();
    }

    @Test
    void listarMetricas_cuandoListaVacia_debeRetornarListaVacia() throws Exception {
        when(metricaBurnoutService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/burnout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ===================== GET BY ID =====================

    @Test
    void obtenerPorId_cuandoExiste_debeRetornar200() throws Exception {
        when(metricaBurnoutService.obtenerPorId(1L)).thenReturn(metrica);

        mockMvc.perform(get("/api/burnout/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(metricaBurnoutService).obtenerPorId(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeRetornar500() throws Exception {
        when(metricaBurnoutService.obtenerPorId(99L))
                .thenThrow(new RuntimeException("No encontrado"));

        mockMvc.perform(get("/api/burnout/99"))
                .andExpect(status().is5xxServerError());
    }

    // ===================== GET BY EMPLEADO =====================

    @Test
    void buscarPorEmpleado_debeRetornarListaFiltrada() throws Exception {
        when(metricaBurnoutService.buscarPorEmpleado(1L)).thenReturn(List.of(metrica));

        mockMvc.perform(get("/api/burnout/empleado/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(metricaBurnoutService).buscarPorEmpleado(1L);
    }

    @Test
    void buscarPorEmpleado_cuandoNoTieneMetricas_debeRetornarListaVacia() throws Exception {
        when(metricaBurnoutService.buscarPorEmpleado(99L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/burnout/empleado/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ===================== POST =====================

    @Test
    void crearMetrica_debeRetornar201() throws Exception {
        when(metricaBurnoutService.guardar(any(MetricaBurnout.class))).thenReturn(metrica);

        mockMvc.perform(post("/api/burnout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(metrica)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));

        verify(metricaBurnoutService).guardar(any(MetricaBurnout.class));
    }

    // ===================== PUT =====================

    @Test
    void actualizar_debeRetornar200() throws Exception {
        when(metricaBurnoutService.actualizar(eq(1L), any(MetricaBurnout.class))).thenReturn(metrica);

        mockMvc.perform(put("/api/burnout/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(metrica)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(metricaBurnoutService).actualizar(eq(1L), any(MetricaBurnout.class));
    }

    // ===================== DELETE =====================

    @Test
    void eliminar_debeRetornar204() throws Exception {
        doNothing().when(metricaBurnoutService).eliminar(1L);

        mockMvc.perform(delete("/api/burnout/1"))
                .andExpect(status().isNoContent());

        verify(metricaBurnoutService).eliminar(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_debeRetornar500() throws Exception {
        doThrow(new RuntimeException("Métrica no encontrada"))
                .when(metricaBurnoutService).eliminar(99L);

        mockMvc.perform(delete("/api/burnout/99"))
                .andExpect(status().is5xxServerError());
    }
}
