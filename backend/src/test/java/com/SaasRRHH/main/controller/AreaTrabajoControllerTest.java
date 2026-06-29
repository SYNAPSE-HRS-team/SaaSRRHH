package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.AreaTrabajo;
import com.SaasRRHH.main.services.AreaTrabajoService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AreaTrabajoController.class)
@WithMockUser
class AreaTrabajoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AreaTrabajoService service;

    @Autowired
    private ObjectMapper objectMapper;

    private AreaTrabajo area;

    @BeforeEach
    void setUp() {
        area = new AreaTrabajo();
        area.setId(1L);
        area.setNombre("Zona Norte");
        area.setCultivoTipo("Espárrago");
        area.setActivo(true);
        area.setFechaRegistro(LocalDateTime.now());
    }

    // ===================== GET ALL =====================

    @Test
    void listar_debeRetornarListaDeAreas() throws Exception {
        when(service.listar()).thenReturn(Arrays.asList(area));

        mockMvc.perform(get("/api/areas-trabajo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Zona Norte")));

        verify(service).listar();
    }

    @Test
    void listar_cuandoListaVacia_debeRetornarListaVacia() throws Exception {
        when(service.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/areas-trabajo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ===================== GET ACTIVAS =====================

    @Test
    void listarActivas_debeRetornarSoloAreasActivas() throws Exception {
        when(service.listarActivas()).thenReturn(Arrays.asList(area));

        mockMvc.perform(get("/api/areas-trabajo/activas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].activo", is(true)));
    }

    // ===================== GET BY ID =====================

    @Test
    void obtener_cuandoExiste_debeRetornar200() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(area));

        mockMvc.perform(get("/api/areas-trabajo/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Zona Norte")));
    }

    @Test
    void obtener_cuandoNoExiste_debeRetornar404() throws Exception {
        when(service.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/areas-trabajo/99"))
                .andExpect(status().isNotFound());
    }

    // ===================== POST =====================

    @Test
    void crear_conDatosValidos_debeRetornar201() throws Exception {
        when(service.guardar(any(AreaTrabajo.class))).thenReturn(area);

        mockMvc.perform(post("/api/areas-trabajo")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(area)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Zona Norte")));
    }

    @Test
    void crear_cuandoAreaYaExiste_debeRetornar400() throws Exception {
        when(service.guardar(any(AreaTrabajo.class)))
                .thenThrow(new RuntimeException("Ya existe un area con ese nombre"));

        mockMvc.perform(post("/api/areas-trabajo")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(area)))
                .andExpect(status().isBadRequest());
    }

    // ===================== PUT =====================

    @Test
    void actualizar_cuandoExiste_debeRetornar200() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(area));
        when(service.guardar(any(AreaTrabajo.class))).thenReturn(area);

        mockMvc.perform(put("/api/areas-trabajo/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(area)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void actualizar_cuandoNoExiste_debeRetornar404() throws Exception {
        when(service.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/areas-trabajo/99")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(area)))
                .andExpect(status().isNotFound());
    }

    // ===================== DELETE =====================

    @Test
    void eliminar_cuandoExiste_debeRetornar204() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/areas-trabajo/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_cuandoNoExiste_debeRetornar404() throws Exception {
        doThrow(new RuntimeException("Area no encontrada"))
                .when(service).eliminar(99L);

        mockMvc.perform(delete("/api/areas-trabajo/99").with(csrf()))
                .andExpect(status().isNotFound());
    }

    // ===================== BUSCAR POR NOMBRE =====================

    @Test
    void buscarPorNombre_cuandoExiste_debeRetornar200() throws Exception {
        when(service.buscarPorNombre("Zona Norte")).thenReturn(Optional.of(area));

        mockMvc.perform(get("/api/areas-trabajo/buscar")
                        .param("nombre", "Zona Norte"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Zona Norte")));
    }

    @Test
    void buscarPorNombre_cuandoNoExiste_debeRetornar404() throws Exception {
        when(service.buscarPorNombre("Inexistente")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/areas-trabajo/buscar")
                        .param("nombre", "Inexistente"))
                .andExpect(status().isNotFound());
    }
}
