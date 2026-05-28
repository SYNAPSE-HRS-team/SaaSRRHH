package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.FamiliarRequestDTO;
import com.SaasRRHH.main.DTO.FamiliarResponseDTO;
import com.SaasRRHH.main.services.FamiliarService;
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

@WebMvcTest(FamiliarController.class)
@WithMockUser
class FamiliarControllerTest {

    @Autowired
    private org.springframework.test.web.servlet.MockMvc mockMvc;

    @MockBean
    private FamiliarService familiarService;

    @Autowired
    private ObjectMapper objectMapper;

    private FamiliarRequestDTO request;
    private FamiliarResponseDTO response;

    @BeforeEach
    void setUp() {
        request = new FamiliarRequestDTO();
        response = new FamiliarResponseDTO();
        response.setId(1L);
        response.setEmpleadoId(100L);
        response.setNombres("Juan Perez");
        response.setParentesco(com.SaasRRHH.main.model.Familiar.Parentesco.PADRE);
        response.setEstudia(false);
        response.setActivo(true);
    }

    @Test
    void listar_debeRetornarLista() throws Exception {
        when(familiarService.listar()).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/api/familiares"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void guardar_debeRetornar201() throws Exception {
        when(familiarService.guardar(org.mockito.ArgumentMatchers.any(FamiliarRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/familiares").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.nombres", is("Juan Perez")));
    }

        @Test
        void buscarPorId_debeRetornar200() throws Exception {
        when(familiarService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/familiares/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.empleadoId", is(100)));
        }

        @Test
        void buscarPorId_noExiste_debeRetornar404() throws Exception {
        when(familiarService.buscarPorId(999L)).thenThrow(new RuntimeException("no encontrado"));

        mockMvc.perform(get("/api/familiares/999"))
            .andExpect(status().isNotFound());
        }

        @Test
        void actualizar_debeRetornar200() throws Exception {
        when(familiarService.actualizar(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any(FamiliarRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/familiares/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
        }

        @Test
        void actualizar_noExiste_debeRetornar404() throws Exception {
        when(familiarService.actualizar(org.mockito.ArgumentMatchers.eq(5L), org.mockito.ArgumentMatchers.any(FamiliarRequestDTO.class))).thenThrow(new RuntimeException("no existe"));

        mockMvc.perform(put("/api/familiares/5").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
        }

        @Test
        void eliminar_debeRetornar204() throws Exception {
        // no throw -> noContent
        mockMvc.perform(delete("/api/familiares/1").with(csrf()))
            .andExpect(status().isNoContent());
        }

        @Test
        void eliminar_noExiste_debeRetornar404() throws Exception {
        org.mockito.Mockito.doThrow(new RuntimeException("no existe")).when(familiarService).eliminar(99L);

        mockMvc.perform(delete("/api/familiares/99").with(csrf()))
            .andExpect(status().isNotFound());
        }

        @Test
        void buscarPorEmpleado_noContent() throws Exception {
        when(familiarService.findByEmpleadoId(200L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/familiares/empleado/200"))
            .andExpect(status().isNoContent());
        }

        @Test
        void buscarPorEmpleado_ok() throws Exception {
        FamiliarResponseDTO otra = new FamiliarResponseDTO();
        otra.setId(2L);
        otra.setEmpleadoId(100L);
        otra.setNombres("Ana");

        when(familiarService.findByEmpleadoId(100L)).thenReturn(Arrays.asList(response, otra));

        mockMvc.perform(get("/api/familiares/empleado/100"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        void listarActivos_debeRetornarLista() throws Exception {
        when(familiarService.listarActivos()).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/api/familiares/activos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        void buscarPorParentesco_debeRetornarLista() throws Exception {
        when(familiarService.buscarPorParentesco(com.SaasRRHH.main.model.Familiar.Parentesco.PADRE)).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/api/familiares/parentesco/PADRE"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        void familiaresQueEstudian_debeRetornarLista() throws Exception {
        when(familiarService.familiaresQueEstudian()).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/api/familiares/estudiantes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        void contarPorParentesco_debeRetornarMatriz() throws Exception {
        Object[] row = new Object[]{"PADRE", 3};
        when(familiarService.contarPorParentesco()).thenReturn(Collections.singletonList(row));

        mockMvc.perform(get("/api/familiares/estadisticas/parentescos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0][0]", is("PADRE")))
            .andExpect(jsonPath("$[0][1]", is(3)));
        }
}
