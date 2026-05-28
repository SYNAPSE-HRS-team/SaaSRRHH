package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.EncuestaBienestarRequestDTO;
import com.SaasRRHH.main.DTO.EncuestaBienestarResponseDTO;
import com.SaasRRHH.main.DTO.ResumenBienestarDTO;
import com.SaasRRHH.main.services.EncuestaBienestarService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EncuestaBienestarController.class)
class EncuestaBienestarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EncuestaBienestarService service;

    @Autowired
    private ObjectMapper objectMapper;

    private EncuestaBienestarResponseDTO encuestaResponse;
    private EncuestaBienestarRequestDTO encuestaRequest;

    @BeforeEach
    void setUp() {
        encuestaResponse = new EncuestaBienestarResponseDTO();
        encuestaResponse.setId(1L);
        encuestaResponse.setEmpleadoId(1L);
        encuestaResponse.setFecha(LocalDate.now());
        encuestaResponse.setCargaLaboral(4);
        encuestaResponse.setApoyoEquipo(3);
        encuestaResponse.setProyeccion(5);
        encuestaResponse.setNivelBienestar("BUENO");
        encuestaResponse.setPromedioGeneral(4.0);

        encuestaRequest = new EncuestaBienestarRequestDTO();
        encuestaRequest.setEmpleadoId(1L);
        encuestaRequest.setFecha(LocalDate.now());
        encuestaRequest.setCargaLaboral(4);
        encuestaRequest.setApoyoEquipo(3);
        encuestaRequest.setProyeccion(5);
    }

    @Test
    void listar_debeRetornarListaDeEncuestas() throws Exception {
        when(service.listar()).thenReturn(Arrays.asList(encuestaResponse));

        mockMvc.perform(get("/api/encuestas-bienestar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void obtener_cuandoExiste_debeRetornar200() throws Exception {
        when(service.obtenerPorId(1L)).thenReturn(encuestaResponse);

        mockMvc.perform(get("/api/encuestas-bienestar/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void obtener_cuandoNoExiste_debeRetornar404() throws Exception {
        when(service.obtenerPorId(99L)).thenReturn(null);

        mockMvc.perform(get("/api/encuestas-bienestar/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crear_conDatosValidos_debeRetornar201() throws Exception {
        when(service.guardar(any(EncuestaBienestarRequestDTO.class))).thenReturn(encuestaResponse);

        mockMvc.perform(post("/api/encuestas-bienestar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(encuestaRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void crear_cuandoEncuestaYaExiste_debeRetornar400() throws Exception {
        when(service.guardar(any(EncuestaBienestarRequestDTO.class)))
                .thenThrow(new IllegalStateException("El empleado ya registró una encuesta en esa fecha"));

        mockMvc.perform(post("/api/encuestas-bienestar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(encuestaRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizar_cuandoExiste_debeRetornar200() throws Exception {
        when(service.actualizar(eq(1L), any(EncuestaBienestarRequestDTO.class))).thenReturn(encuestaResponse);

        mockMvc.perform(put("/api/encuestas-bienestar/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(encuestaRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_cuandoExiste_debeRetornar204() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/encuestas-bienestar/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void historialEmpleado_debeRetornarHistorial() throws Exception {
        when(service.obtenerHistorialEmpleado(1L)).thenReturn(Arrays.asList(encuestaResponse));

        mockMvc.perform(get("/api/encuestas-bienestar/empleado/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void porRango_debeRetornarFiltrado() throws Exception {
        when(service.obtenerPorRangoFechas(any(), any())).thenReturn(Arrays.asList(encuestaResponse));

        mockMvc.perform(get("/api/encuestas-bienestar/rango")
                        .param("inicio", "2025-01-01")
                        .param("fin", "2025-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void empleadosEnRiesgo_debeRetornarIds() throws Exception {
        when(service.obtenerEmpleadosEnRiesgo()).thenReturn(Arrays.asList(1L, 2L));

        mockMvc.perform(get("/api/encuestas-bienestar/riesgo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void resumen_debeRetornarResumenMensual() throws Exception {
        ResumenBienestarDTO resumen = new ResumenBienestarDTO();
        resumen.setTotalEncuestas(10L);
        resumen.setPromedioGeneral(3.8);
        resumen.setPulsoOrganizacional("REGULAR");

        when(service.obtenerResumenMensual(any(), any())).thenReturn(resumen);

        mockMvc.perform(get("/api/encuestas-bienestar/resumen")
                        .param("inicio", "2025-01-01")
                        .param("fin", "2025-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEncuestas", is(10)));
    }
}
