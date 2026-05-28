package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.AccesoUsuarioRequestDTO;
import com.SaasRRHH.main.DTO.AccesoUsuarioResponseDTO;
import com.SaasRRHH.main.security.JwtUtil;
import com.SaasRRHH.main.services.AccesoUsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccesoUsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)

@WithMockUser
class AccesoUsuarioControllerTest {

    @Autowired
    private org.springframework.test.web.servlet.MockMvc mockMvc;

    @MockBean
    private AccesoUsuarioService service;
            @MockBean
        private JwtUtil jwtUtil;

        @MockBean
        private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private AccesoUsuarioRequestDTO request;
    private AccesoUsuarioResponseDTO response;

    @BeforeEach
    void setUp() {
        request = new AccesoUsuarioRequestDTO();
        response = new AccesoUsuarioResponseDTO();
        response.setIdAcceso(1L);
        response.setUsuarioId(99L);
        response.setFechaLogin(LocalDateTime.now());
        response.setExitoso(true);
    }

    @Test
    void listar_debeRetornarLista() throws Exception {
        when(service.listar()).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/api/accesos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void guardar_debeRetornar201() throws Exception {
        when(service.guardar(org.mockito.ArgumentMatchers.any(AccesoUsuarioRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/accesos").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.idAcceso", is(1)))
            .andExpect(jsonPath("$.usuarioId", is(99)))
            .andExpect(jsonPath("$.exitoso", is(true)));
    }

        @Test
        void buscarPorId_debeRetornar200() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/accesos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idAcceso", is(1)));
        }

        @Test
        void porUsuario_debeRetornarLista() throws Exception {
        AccesoUsuarioResponseDTO otra = new AccesoUsuarioResponseDTO();
        otra.setIdAcceso(2L);
        otra.setUsuarioId(99L);

        when(service.buscarPorUsuario(99L)).thenReturn(Arrays.asList(response, otra));

        mockMvc.perform(get("/api/accesos/usuario/99"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        void accesosOrdenados_debeRetornarLista() throws Exception {
        when(service.listarOrdenadosPorUsuario(99L)).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/api/accesos/ordenados/99"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        void usuariosMasActivos_y_accesosExitosos_debenRetornarMatriz() throws Exception {
        Object[] row = new Object[]{99L, 5L};
        when(service.usuariosMasActivos()).thenReturn(Collections.singletonList(row));
        when(service.accesosExitososPorUsuario()).thenReturn(Collections.singletonList(new Object[]{99L, 4L}));

        mockMvc.perform(get("/api/accesos/top-usuarios"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/accesos/exitosos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        void fallidos_y_activos_y_ultimoAcceso_debenResponder() throws Exception {
        when(service.listarFallidos()).thenReturn(Arrays.asList(response));
        when(service.sesionesActivas()).thenReturn(Arrays.asList(response));
        when(service.ultimoAccesoUsuario(99L)).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/api/accesos/fallidos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/accesos/activos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/accesos/ultimo-acceso/99"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
        }
}
