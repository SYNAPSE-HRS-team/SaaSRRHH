package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.MetricaBurnoutResponseDTO;
import com.SaasRRHH.main.security.JwtUtil;
import com.SaasRRHH.main.services.MetricaBurnoutService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    private MetricaBurnoutResponseDTO metricaDTO;

    @BeforeEach
    void setUp() {
        metricaDTO = new MetricaBurnoutResponseDTO();
        metricaDTO.setId(1L);
        metricaDTO.setEmpleadoId(1L);
        metricaDTO.setNombreEmpleado("Juan Pérez");
        metricaDTO.setNivelRiesgo("MEDIO");
        metricaDTO.setHorasExtraAcumuladas(10);
        metricaDTO.setTendenciaTardanza(false);
        metricaDTO.setFechaEvaluacion(LocalDateTime.now());
    }

    // ===================== GET ALL =====================

    @Test
    void listarMetricas_debeRetornarLista() throws Exception {
        List<MetricaBurnoutResponseDTO> metricas = Arrays.asList(metricaDTO);
        when(metricaBurnoutService.listar()).thenReturn(metricas);

        mockMvc.perform(get("/api/burnout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombreEmpleado", is("Juan Pérez")))
                .andExpect(jsonPath("$[0].nivelRiesgo", is("MEDIO")));

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
        when(metricaBurnoutService.obtenerPorId(1L)).thenReturn(metricaDTO);

        mockMvc.perform(get("/api/burnout/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombreEmpleado", is("Juan Pérez")));

        verify(metricaBurnoutService).obtenerPorId(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeRetornar500() throws Exception {
        when(metricaBurnoutService.obtenerPorId(99L))
                .thenThrow(new RuntimeException("Métrica no encontrada"));

        mockMvc.perform(get("/api/burnout/99"))
                .andExpect(status().is5xxServerError());
    }

    // ===================== GET BY EMPLEADO =====================

    @Test
    void buscarPorEmpleado_debeRetornarListaFiltrada() throws Exception {
        when(metricaBurnoutService.buscarPorEmpleado(1L)).thenReturn(List.of(metricaDTO));

        mockMvc.perform(get("/api/burnout/empleado/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].empleadoId", is(1)));

        verify(metricaBurnoutService).buscarPorEmpleado(1L);
    }

    @Test
    void buscarPorEmpleado_cuandoNoTieneMetricas_debeRetornarListaVacia() throws Exception {
        when(metricaBurnoutService.buscarPorEmpleado(99L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/burnout/empleado/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ===================== GET ÚLTIMO NIVEL DE RIESGO =====================

    @Test
    void obtenerUltimoNivelRiesgo_debeRetornarString() throws Exception {
        when(metricaBurnoutService.obtenerUltimoNivelRiesgo(1L)).thenReturn("MEDIO");

        mockMvc.perform(get("/api/burnout/empleado/1/ultimo"))
                .andExpect(status().isOk())
                .andExpect(content().string("MEDIO"));

        verify(metricaBurnoutService).obtenerUltimoNivelRiesgo(1L);
    }

    @Test
    void obtenerUltimoNivelRiesgo_cuandoNoTieneMetricas_debeRetornarSIN_EVALUACION() throws Exception {
        when(metricaBurnoutService.obtenerUltimoNivelRiesgo(99L)).thenReturn("SIN_EVALUACION");

        mockMvc.perform(get("/api/burnout/empleado/99/ultimo"))
                .andExpect(status().isOk())
                .andExpect(content().string("SIN_EVALUACION"));
    }

    // ===================== GET HISTORIAL COMPLETO =====================

    @Test
    void obtenerHistorial_debeRetornarLista() throws Exception {
        List<MetricaBurnoutResponseDTO> historial = Arrays.asList(metricaDTO);
        when(metricaBurnoutService.obtenerHistorialCompleto(1L)).thenReturn(historial);

        mockMvc.perform(get("/api/burnout/empleado/1/historial"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(metricaBurnoutService).obtenerHistorialCompleto(1L);
    }

    // ===================== POST CALCULAR =====================

    @Test
    void calcularMetrica_debeRetornar201() throws Exception {
        when(metricaBurnoutService.calcularMetrica(1L)).thenReturn(metricaDTO);

        mockMvc.perform(post("/api/burnout/calcular/1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nivelRiesgo", is("MEDIO")));

        verify(metricaBurnoutService).calcularMetrica(1L);
    }

    @Test
    void calcularMetrica_cuandoEmpleadoNoExiste_debeRetornar500() throws Exception {
        when(metricaBurnoutService.calcularMetrica(99L))
                .thenThrow(new RuntimeException("Empleado no encontrado"));

        mockMvc.perform(post("/api/burnout/calcular/99"))
                .andExpect(status().is5xxServerError());
    }

    // ===================== POST RECALCULAR TODAS =====================

    @Test
    void recalcularTodas_debeRetornarLista() throws Exception {
        List<MetricaBurnoutResponseDTO> metricas = Arrays.asList(metricaDTO);
        when(metricaBurnoutService.recalcularTodas()).thenReturn(metricas);

        mockMvc.perform(post("/api/burnout/recalcular-todas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(metricaBurnoutService).recalcularTodas();
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