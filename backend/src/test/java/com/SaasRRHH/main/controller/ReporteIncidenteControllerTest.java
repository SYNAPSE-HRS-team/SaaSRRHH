package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.ReporteIncidenteRequestDTO;
import com.SaasRRHH.main.DTO.ReporteIncidenteResponseDTO;
import com.SaasRRHH.main.security.JwtUtil;
import com.SaasRRHH.main.services.ReporteIncidenteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReporteIncidenteController.class)
@AutoConfigureMockMvc(addFilters = false)

@WithMockUser
class ReporteIncidenteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteIncidenteService service;
                @MockBean
        private JwtUtil jwtUtil;

        @MockBean
        private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private ReporteIncidenteResponseDTO reporteResponse;
    private ReporteIncidenteRequestDTO reporteRequest;

    @BeforeEach
    void setUp() {
        reporteResponse = new ReporteIncidenteResponseDTO();
        reporteResponse.setId(1L);
        reporteResponse.setEmpleadoId(1L);
        reporteResponse.setTipo("INCIDENTE");
        reporteResponse.setDescripcion("Caída en zona de trabajo");
        reporteResponse.setNivelRiesgo("ALTO");
        reporteResponse.setEstado("REPORTADO");
        reporteResponse.setFechaIncidente(LocalDateTime.now());
        reporteResponse.setFechaRegistro(LocalDateTime.now());

        reporteRequest = new ReporteIncidenteRequestDTO();
        reporteRequest.setEmpleadoId(1L);
        reporteRequest.setTipo("INCIDENTE");
        reporteRequest.setDescripcion("Caída en zona de trabajo");
        reporteRequest.setNivelRiesgo("ALTO");
        reporteRequest.setEstado("REPORTADO");
        reporteRequest.setFechaIncidente(LocalDateTime.now());
    }

    // ===================== GET ALL =====================

    @Test
    void listar_debeRetornarListaDeReportes() throws Exception {
        when(service.listar()).thenReturn(Arrays.asList(reporteResponse));

        mockMvc.perform(get("/api/reportes-incidentes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].tipo", is("INCIDENTE")));
    }

    @Test
    void listar_cuandoVacia_debeRetornarListaVacia() throws Exception {
        when(service.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/reportes-incidentes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ===================== GET BY ID =====================

    @Test
    void obtener_cuandoExiste_debeRetornar200() throws Exception {
        when(service.obtenerPorId(1L)).thenReturn(reporteResponse);

        mockMvc.perform(get("/api/reportes-incidentes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void obtener_cuandoNoExiste_debeRetornar500() throws Exception {
        when(service.obtenerPorId(99L)).thenThrow(new RuntimeException("No encontrado"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(get("/api/reportes-incidentes/99")).andReturn()
        );
    }

    // ===================== POST =====================

    @Test
    void crear_conDatosValidos_debeRetornar200() throws Exception {
        when(service.guardar(any(ReporteIncidenteRequestDTO.class))).thenReturn(reporteResponse);

        mockMvc.perform(post("/api/reportes-incidentes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reporteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    // ===================== PUT =====================

    @Test
    void actualizar_cuandoExiste_debeRetornar200() throws Exception {
        when(service.actualizar(eq(1L), any(ReporteIncidenteRequestDTO.class))).thenReturn(reporteResponse);

        mockMvc.perform(put("/api/reportes-incidentes/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reporteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    // ===================== DELETE =====================

    @Test
    void eliminar_cuandoExiste_debeRetornar200() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/reportes-incidentes/1").with(csrf()))
                .andExpect(status().isOk());
    }

    // ===================== CONSULTAS ESPECIALIZADAS =====================

    @Test
    void porEmpleado_debeRetornarReportesDelEmpleado() throws Exception {
        when(service.listarPorEmpleado(1L)).thenReturn(Arrays.asList(reporteResponse));

        mockMvc.perform(get("/api/reportes-incidentes/empleado/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void porRango_debeRetornarFiltrado() throws Exception {
        when(service.buscarPorRangoFechas(any(), any())).thenReturn(Arrays.asList(reporteResponse));

        mockMvc.perform(get("/api/reportes-incidentes/rango")
                        .param("inicio", "2025-01-01T00:00:00")
                        .param("fin", "2025-12-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void porRiesgo_debeRetornarFiltradoPorNivel() throws Exception {
        when(service.listarPorNivelRiesgo("ALTO")).thenReturn(Arrays.asList(reporteResponse));

        mockMvc.perform(get("/api/reportes-incidentes/riesgo/ALTO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void porEstado_debeRetornarFiltrado() throws Exception {
        when(service.listarPorEstado("REPORTADO")).thenReturn(Arrays.asList(reporteResponse));

        mockMvc.perform(get("/api/reportes-incidentes/estado/REPORTADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void criticos_debeRetornarIncidentesCriticos() throws Exception {
        when(service.incidentesCriticos()).thenReturn(Arrays.asList(reporteResponse));

        mockMvc.perform(get("/api/reportes-incidentes/criticos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void deHoy_debeRetornarIncidentesDelDia() throws Exception {
        when(service.incidentesDeHoy()).thenReturn(Arrays.asList(reporteResponse));

        mockMvc.perform(get("/api/reportes-incidentes/hoy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void criticosDetalle_debeRetornarDetalle() throws Exception {
        when(service.incidentesCriticosConDetalle()).thenReturn(Arrays.asList(reporteResponse));

        mockMvc.perform(get("/api/reportes-incidentes/criticos-detalle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void statsEmpleado_debeRetornarEstadisticas() throws Exception {
        when(service.incidentesPorEmpleado()).thenReturn(Collections.singletonList(new Object[]{1L, 3L}));

        mockMvc.perform(get("/api/reportes-incidentes/stats/empleado"))
                .andExpect(status().isOk());
    }

    @Test
    void statsRiesgo_debeRetornarEstadisticas() throws Exception {
        when(service.incidentesPorRiesgo()).thenReturn(Collections.singletonList(new Object[]{"ALTO", 5L}));

        mockMvc.perform(get("/api/reportes-incidentes/stats/riesgo"))
                .andExpect(status().isOk());
    }
}
