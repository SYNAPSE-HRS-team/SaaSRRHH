package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.DispositivoAutorizadoRequestDTO;
import com.SaasRRHH.main.DTO.DispositivoAutorizadoResponseDTO;
import com.SaasRRHH.main.services.DispositivoAutorizadoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DispositivoAutorizadoController.class)
@WithMockUser
class DispositivoAutorizadoControllerTest {

    @Autowired
    private org.springframework.test.web.servlet.MockMvc mockMvc;

    @MockBean
    private DispositivoAutorizadoService service;

    @Autowired
    private ObjectMapper objectMapper;

    private DispositivoAutorizadoRequestDTO request;
    private DispositivoAutorizadoResponseDTO response;

    @BeforeEach
    void setUp() {
        request = new DispositivoAutorizadoRequestDTO();
        response = new DispositivoAutorizadoResponseDTO();
        response.setId(1L);
        response.setUsuarioId(42L);
        response.setHardwareId("HW-ABC-123");
        response.setActivo(true);
    }

    @Test
    void listar_debeRetornarLista() throws Exception {
        when(service.listarTodo()).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/api/dispositivos-autorizados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void guardar_debeRetornar201() throws Exception {
        when(service.guardar(org.mockito.ArgumentMatchers.any(DispositivoAutorizadoRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/dispositivos-autorizados").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.hardwareId", is("HW-ABC-123")));
    }

        @Test
        void buscarPorId_debeRetornar200() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/dispositivos-autorizados/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.usuarioId", is(42)));
        }

        @Test
        void actualizar_debeRetornar200() throws Exception {
        when(service.actualizar(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(put("/api/dispositivos-autorizados/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
        }

        @Test
        void eliminar_debeRetornar204() throws Exception {
        mockMvc.perform(delete("/api/dispositivos-autorizados/1").with(csrf()))
            .andExpect(status().isNoContent());
        }

        @Test
        void listarActivos_debeRetornarLista() throws Exception {
        when(service.listarActivos()).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/api/dispositivos-autorizados/activos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        void buscarPorUsuario_debeRetornarLista() throws Exception {
        when(service.buscarPorUsuario(42L)).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/api/dispositivos-autorizados/usuario/42"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].usuarioId", is(42)));
        }

        @Test
        void existeHardwareRegistrado_debeRetornarBoolean() throws Exception {
        when(service.existeHardwareRegistrado(42L, "HW-ABC-123")).thenReturn(true);

        mockMvc.perform(get("/api/dispositivos-autorizados/existe").param("usuarioId","42").param("hardwareId","HW-ABC-123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(true)));
        }

        @Test
        void dispositivosRecientes_debeRetornarLista() throws Exception {
        when(service.dispositivosRecientes()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/dispositivos-autorizados/recientes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
        }
}
