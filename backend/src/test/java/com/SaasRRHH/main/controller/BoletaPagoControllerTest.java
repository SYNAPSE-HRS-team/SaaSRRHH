package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.BoletaPago;
import com.SaasRRHH.main.services.BoletaPagoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

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

@WebMvcTest(BoletaPagoController.class)
@WithMockUser
class BoletaPagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoletaPagoService service;

    @Autowired
    private ObjectMapper objectMapper;

    private BoletaPago boleta;

    @BeforeEach
    void setUp() {
        boleta = new BoletaPago();
        boleta.setId(1L);
        boleta.setSueldoBase(new BigDecimal("1500.00"));
        boleta.setDiasTrabajados(22);
        boleta.setDiasNoTrabajados(0);
        boleta.setNetoPagar(new BigDecimal("1500.00"));
        boleta.setTotalIngresos(new BigDecimal("1500.00"));
        boleta.setTotalDescuentos(BigDecimal.ZERO);
    }

    // ===================== GET ALL =====================

    @Test
    void listar_debeRetornarListaDeBoletas() throws Exception {
        when(service.listar()).thenReturn(Arrays.asList(boleta));

        mockMvc.perform(get("/api/boletas_pago"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));

        verify(service).listar();
    }

    @Test
    void listar_cuandoVacia_debeRetornarListaVacia() throws Exception {
        when(service.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/boletas_pago"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ===================== GET BY ID =====================

    @Test
    void buscar_cuandoExiste_debeRetornar200() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(boleta));

        mockMvc.perform(get("/api/boletas_pago/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void buscar_cuandoNoExiste_debeRetornar404() throws Exception {
        when(service.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/boletas_pago/99"))
                .andExpect(status().isNotFound());
    }

    // ===================== POST =====================

    @Test
    void crear_debeRetornar200YBoletaCreada() throws Exception {
        when(service.guardar(any(BoletaPago.class))).thenReturn(boleta);

        mockMvc.perform(post("/api/boletas_pago")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boleta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(service).guardar(any(BoletaPago.class));
    }

    // ===================== PUT =====================

    @Test
    void actualizar_cuandoExiste_debeRetornar200() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(boleta));
        when(service.actualizar(eq(1L), any(BoletaPago.class))).thenReturn(boleta);

        mockMvc.perform(put("/api/boletas_pago/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boleta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void actualizar_cuandoNoExiste_debeRetornar404() throws Exception {
        when(service.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/boletas_pago/99")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boleta)))
                .andExpect(status().isNotFound());
    }

    // ===================== DELETE =====================

    @Test
    void eliminar_cuandoExiste_debeRetornar204() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(boleta));
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/boletas_pago/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_cuandoNoExiste_debeRetornar404() throws Exception {
        when(service.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/boletas_pago/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
