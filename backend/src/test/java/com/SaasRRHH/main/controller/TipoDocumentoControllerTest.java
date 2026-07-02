package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.TipoDocumentoRequestDTO;
import com.SaasRRHH.main.DTO.TipoDocumentoResponseDTO;
import com.SaasRRHH.main.security.JwtUtil;
import com.SaasRRHH.main.services.TipoDocumentoService;
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

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TipoDocumentoController.class)
@AutoConfigureMockMvc(addFilters = false)

@WithMockUser
class TipoDocumentoControllerTest {

    @Autowired
    private org.springframework.test.web.servlet.MockMvc mockMvc;

    @MockBean
    private TipoDocumentoService service;
                @MockBean
        private JwtUtil jwtUtil;

        @MockBean
        private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private TipoDocumentoRequestDTO request;
    private TipoDocumentoResponseDTO response;

    @BeforeEach
    void setUp() {
        request = new TipoDocumentoRequestDTO();
        response = new TipoDocumentoResponseDTO();
        response.setIdTipo(1L);
    }

    @Test
    void listar_debeRetornarLista() throws Exception {
        when(service.listar()).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/api/tipos-documento"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void crear_debeRetornar201() throws Exception {
        when(service.guardar(org.mockito.ArgumentMatchers.any(TipoDocumentoRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/tipos-documento").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idTipo", is(1)));
    }
}
