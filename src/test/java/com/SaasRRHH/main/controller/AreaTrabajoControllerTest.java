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

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AreaTrabajoController.class)
class AreaTrabajoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AreaTrabajoService areaService;

    @Autowired
    private ObjectMapper objectMapper;

    private AreaTrabajo areaNorte;
    private AreaTrabajo areaSur;

    @BeforeEach
    void setUp() {
        areaNorte = new AreaTrabajo();
        areaNorte.setId(1L);
        areaNorte.setNombre("Parcela Norte");
        areaNorte.setCultivoTipo("Papa");
        areaNorte.setActivo(true);

        areaSur = new AreaTrabajo();
        areaSur.setId(2L);
        areaSur.setNombre("Parcela Sur");
        areaSur.setCultivoTipo("Maíz");
        areaSur.setActivo(true);
    }

    // ========== TEST GET ALL ==========
    @Test
    void testListarAreas_Exitoso() throws Exception {
        when(areaService.listar()).thenReturn(Arrays.asList(areaNorte, areaSur));

        mockMvc.perform(get("/api/areas-trabajo")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Parcela Norte"))
                .andExpect(jsonPath("$[1].nombre").value("Parcela Sur"));

        verify(areaService, times(1)).listar();
    }

    @Test
    void testListarAreas_Vacio() throws Exception {
        when(areaService.listar()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/areas-trabajo")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(areaService, times(1)).listar();
    }

    // ========== TEST LISTAR ACTIVAS ==========
    @Test
    void testListarAreasActivas_Exitoso() throws Exception {
        when(areaService.listarActivas()).thenReturn(Arrays.asList(areaNorte, areaSur));

        mockMvc.perform(get("/api/areas-trabajo/activas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(areaService, times(1)).listarActivas();
    }

    // ========== TEST GET BY ID ==========
    @Test
    void testObtenerAreaPorId_Encontrado() throws Exception {
        when(areaService.buscarPorId(1L)).thenReturn(Optional.of(areaNorte));

        mockMvc.perform(get("/api/areas-trabajo/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Parcela Norte"))
                .andExpect(jsonPath("$.cultivoTipo").value("Papa"));

        verify(areaService, times(1)).buscarPorId(1L);
    }

    @Test
    void testObtenerAreaPorId_NoEncontrado() throws Exception {
        when(areaService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/areas-trabajo/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(areaService, times(1)).buscarPorId(99L);
    }

    // ========== TEST POST CREATE ==========
    @Test
    void testCrearArea_Exitoso() throws Exception {
        AreaTrabajo nuevaArea = new AreaTrabajo();
        nuevaArea.setNombre("Parcela Este");
        nuevaArea.setCultivoTipo("Tomate");
        nuevaArea.setActivo(true);

        AreaTrabajo areaCreada = new AreaTrabajo();
        areaCreada.setId(3L);
        areaCreada.setNombre("Parcela Este");
        areaCreada.setCultivoTipo("Tomate");
        areaCreada.setActivo(true);

        when(areaService.guardar(any(AreaTrabajo.class))).thenReturn(areaCreada);

        mockMvc.perform(post("/api/areas-trabajo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevaArea)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.nombre").value("Parcela Este"))
                .andExpect(jsonPath("$.cultivoTipo").value("Tomate"));

        verify(areaService, times(1)).guardar(any(AreaTrabajo.class));
    }

    @Test
    void testCrearArea_NombreDuplicado_RetornaBadRequest() throws Exception {
        AreaTrabajo areaDuplicada = new AreaTrabajo();
        areaDuplicada.setNombre("Parcela Norte");

        when(areaService.guardar(any(AreaTrabajo.class)))
            .thenThrow(new RuntimeException("Ya existe un área con el nombre: Parcela Norte"));

        mockMvc.perform(post("/api/areas-trabajo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(areaDuplicada)))
                .andExpect(status().isBadRequest());

        verify(areaService, times(1)).guardar(any(AreaTrabajo.class));
    }

    // ========== TEST PUT UPDATE ==========
    @Test
    void testActualizarArea_Exitoso() throws Exception {
        AreaTrabajo areaActualizada = new AreaTrabajo();
        areaActualizada.setNombre("Parcela Norte");
        areaActualizada.setCultivoTipo("Papa Actualizada");
        areaActualizada.setActivo(true);

        AreaTrabajo areaActualizadaConId = new AreaTrabajo();
        areaActualizadaConId.setId(1L);
        areaActualizadaConId.setNombre("Parcela Norte");
        areaActualizadaConId.setCultivoTipo("Papa Actualizada");
        areaActualizadaConId.setActivo(true);

        when(areaService.buscarPorId(1L)).thenReturn(Optional.of(areaNorte));
        when(areaService.guardar(any(AreaTrabajo.class))).thenReturn(areaActualizadaConId);

        mockMvc.perform(put("/api/areas-trabajo/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(areaActualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cultivoTipo").value("Papa Actualizada"));

        verify(areaService, times(1)).buscarPorId(1L);
        verify(areaService, times(1)).guardar(any(AreaTrabajo.class));
    }

    @Test
    void testActualizarArea_NoEncontrada_RetornaNotFound() throws Exception {
        AreaTrabajo areaActualizada = new AreaTrabajo();
        areaActualizada.setNombre("Parcela Norte");

        when(areaService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/areas-trabajo/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(areaActualizada)))
                .andExpect(status().isNotFound());

        verify(areaService, times(1)).buscarPorId(99L);
        verify(areaService, never()).guardar(any(AreaTrabajo.class));
    }

    // ========== TEST DELETE ==========
    @Test
    void testEliminarArea_Exitoso() throws Exception {
        doNothing().when(areaService).eliminar(1L);

        mockMvc.perform(delete("/api/areas-trabajo/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(areaService, times(1)).eliminar(1L);
    }

    @Test
    void testEliminarArea_NoEncontrada_RetornaNotFound() throws Exception {
        doThrow(new RuntimeException("Área no encontrada con id: 99")).when(areaService).eliminar(99L);

        mockMvc.perform(delete("/api/areas-trabajo/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(areaService, times(1)).eliminar(99L);
    }

    // ========== TEST BUSCAR POR NOMBRE ==========
    @Test
    void testBuscarAreaPorNombre_Encontrado() throws Exception {
        when(areaService.buscarPorNombre("Parcela Norte")).thenReturn(Optional.of(areaNorte));

        mockMvc.perform(get("/api/areas-trabajo/buscar")
                .param("nombre", "Parcela Norte")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Parcela Norte"));

        verify(areaService, times(1)).buscarPorNombre("Parcela Norte");
    }

    @Test
    void testBuscarAreaPorNombre_NoEncontrado_RetornaNotFound() throws Exception {
        when(areaService.buscarPorNombre("INEXISTENTE")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/areas-trabajo/buscar")
                .param("nombre", "INEXISTENTE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(areaService, times(1)).buscarPorNombre("INEXISTENTE");
    }
}