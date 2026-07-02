package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.DocumentoPrivadoRequestDTO;
import com.SaasRRHH.main.DTO.DocumentoPrivadoResponseDTO;
import com.SaasRRHH.main.security.JwtUtil;
import com.SaasRRHH.main.services.DocumentoPrivadoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentoPrivadoController.class)
@WithMockUser
class DocumentoPrivadoControllerTest {

    @Autowired
    private org.springframework.test.web.servlet.MockMvc mockMvc;

    @MockBean
    private DocumentoPrivadoService service;
            @MockBean
        private JwtUtil jwtUtil;

        @MockBean
        private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private DocumentoPrivadoRequestDTO request;
    private DocumentoPrivadoResponseDTO response;

    @BeforeEach
    void setUp() {
        request = new DocumentoPrivadoRequestDTO();
        response = new DocumentoPrivadoResponseDTO();
        response.setId(1L);
        response.setEmpleadoId(50L);
        response.setTipoId(2L);
        response.setArchivoUrl("/files/doc1.pdf");
        response.setFechaVencimiento(LocalDate.of(2025,12,31));
        response.setActivo(true);
    }

    @Test
    void listar_debeRetornarLista() throws Exception {
        when(service.listar()).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/api/documentos-privados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void guardar_debeRetornar201() throws Exception {
        when(service.guardar(org.mockito.ArgumentMatchers.any(DocumentoPrivadoRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/documentos-privados").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.archivoUrl", is("/files/doc1.pdf")));
    }

        @Test
        void buscarPorId_debeRetornar200() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/documentos-privados/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.empleadoId", is(50)));
        }

        @Test
        void buscarPorId_noExiste_debeRetornar404() throws Exception {
        when(service.buscarPorId(999L)).thenThrow(new RuntimeException("no existe"));

        mockMvc.perform(get("/api/documentos-privados/999"))
            .andExpect(status().isNotFound());
        }

        @Test
        void listarActivos_debeRetornarLista() throws Exception {
        when(service.listarActivos()).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/api/documentos-privados/activos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        void buscarPorEmpleado_debeRetornarLista() throws Exception {
        when(service.buscarPorEmpleado(50L)).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/api/documentos-privados/empleado/50"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].empleadoId", is(50)));
        }

        @Test
        void buscarPorTipo_debeRetornarLista() throws Exception {
        when(service.buscarPorTipo(2L)).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/api/documentos-privados/tipo/2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].tipoId", is(2)));
        }

        @Test
        void documentosPorVencer_debeAceptarFecha() throws Exception {
        when(service.documentosPorVencer(LocalDate.of(2025,12,31))).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/api/documentos-privados/por-vencer").param("fechaLimite","2025-12-31"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        void contarDocumentosPorTipo_debeRetornarMatriz() throws Exception {
        Object[] row = new Object[]{"TIPO_X", 4};
        when(service.contarDocumentosPorTipo()).thenReturn(Collections.singletonList(row));

        mockMvc.perform(get("/api/documentos-privados/estadisticas/tipos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0][0]", is("TIPO_X")));
        }
}
