package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.Planilla;
import com.SaasRRHH.main.services.PlanillaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlanillaController.class)
class PlanillaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlanillaService planillaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Planilla planilla;

    @BeforeEach
    void setUp() {
        planilla = new Planilla();
        planilla.setId(1L);
        planilla.setMes(5);
        planilla.setAnio(2025);
        planilla.setTotalPagado(new BigDecimal("15000.00"));
        planilla.setEstado(Planilla.EstadoPlanilla.PROCESADO);
    }

    // ===================== GET ALL =====================

    @Test
    void listar_debeRetornarListaDePlanillas() throws Exception {
        when(planillaService.listar()).thenReturn(Arrays.asList(planilla));

        mockMvc.perform(get("/api/planillas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].mes", is(5)))
                .andExpect(jsonPath("$[0].anio", is(2025)));

        verify(planillaService).listar();
    }

    @Test
    void listar_cuandoListaVacia_debeRetornarListaVacia() throws Exception {
        when(planillaService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/planillas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ===================== GET BY ID =====================

    @Test
    void buscarPorId_cuandoExiste_debeRetornar200() throws Exception {
        when(planillaService.buscarPorId(1L)).thenReturn(Optional.of(planilla));

        mockMvc.perform(get("/api/planillas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.mes", is(5)));
    }

    @Test
    void buscarPorId_cuandoNoExiste_debeRetornar404() throws Exception {
        when(planillaService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/planillas/99"))
                .andExpect(status().isNotFound());
    }

    // ===================== POST =====================

    @Test
    void guardar_conDatosValidos_debeRetornar201() throws Exception {
        when(planillaService.guardar(any(Planilla.class))).thenReturn(planilla);

        mockMvc.perform(post("/api/planillas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planilla)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.mes", is(5)));

        verify(planillaService).guardar(any(Planilla.class));
    }

    // ===================== PUT =====================

    @Test
    void actualizar_cuandoExiste_debeRetornar200() throws Exception {
        when(planillaService.actualizar(eq(1L), any(Planilla.class))).thenReturn(planilla);

        mockMvc.perform(put("/api/planillas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planilla)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(planillaService).actualizar(eq(1L), any(Planilla.class));
    }

    @Test
    void actualizar_cuandoNoExiste_debeRetornar404() throws Exception {
        when(planillaService.actualizar(eq(99L), any(Planilla.class)))
                .thenThrow(new RuntimeException("Planilla no encontrada"));

        mockMvc.perform(put("/api/planillas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planilla)))
                .andExpect(status().isNotFound());
    }

    // ===================== DELETE =====================

    @Test
    void eliminar_cuandoExiste_debeRetornar204() throws Exception {
        doNothing().when(planillaService).eliminar(1L);

        mockMvc.perform(delete("/api/planillas/1"))
                .andExpect(status().isNoContent());

        verify(planillaService).eliminar(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_debeRetornar404() throws Exception {
        doThrow(new RuntimeException("Planilla no encontrada"))
                .when(planillaService).eliminar(99L);

        mockMvc.perform(delete("/api/planillas/99"))
                .andExpect(status().isNotFound());
    }
}
