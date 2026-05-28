package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.ReporteDiarioRequestDTO;
import com.SaasRRHH.main.DTO.ReporteDiarioResponseDTO;
import com.SaasRRHH.main.services.ReporteDiarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReporteDiarioController.class)
@WithMockUser
class ReporteDiarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteDiarioService service;

    @Autowired
    private ObjectMapper objectMapper;

    private ReporteDiarioResponseDTO reporteResponse;
    private ReporteDiarioRequestDTO reporteRequest;

    @BeforeEach
    void setUp() {
        reporteResponse = new ReporteDiarioResponseDTO();
        reporteResponse.setId(1L);
        reporteResponse.setTareaId(1L);
        reporteResponse.setEmpleadoId(1L);
        reporteResponse.setDescripcionTrabajador("Trabajo completado");
        reporteResponse.setPorcentajeAvance(new BigDecimal("75.00"));
        reporteResponse.setEstado("PENDIENTE");
        reporteResponse.setFechaReporte(LocalDateTime.now());

        reporteRequest = new ReporteDiarioRequestDTO();
        reporteRequest.setTareaId(1L);
        reporteRequest.setEmpleadoId(1L);
        reporteRequest.setDescripcionTrabajador("Trabajo completado");
        reporteRequest.setPorcentajeAvance(new BigDecimal("75.00"));
        reporteRequest.setEstado("PENDIENTE");
    }

    // ===================== GET ALL =====================

    @Test
    void listar_debeRetornarListaDeReportes() throws Exception {
        when(service.listar()).thenReturn(Arrays.asList(reporteResponse));

        mockMvc.perform(get("/api/reportes-diarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void listar_cuandoVacia_debeRetornarListaVacia() throws Exception {
        when(service.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/reportes-diarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ===================== GET BY ID =====================

    @Test
    void buscarPorId_cuandoExiste_debeRetornar200() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(reporteResponse);

        mockMvc.perform(get("/api/reportes-diarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void buscarPorId_cuandoNoExiste_debeRetornar500() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new RuntimeException("Reporte no encontrado"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(get("/api/reportes-diarios/99")).andReturn()
        );
    }

    // ===================== POST =====================

    @Test
    void guardar_conDatosValidos_debeRetornar201() throws Exception {
        when(service.guardar(any(ReporteDiarioRequestDTO.class))).thenReturn(reporteResponse);

        mockMvc.perform(post("/api/reportes-diarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reporteRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    // ===================== PUT =====================

    @Test
    void actualizar_cuandoExiste_debeRetornar200() throws Exception {
        when(service.actualizar(eq(1L), any(ReporteDiarioRequestDTO.class))).thenReturn(reporteResponse);

        mockMvc.perform(put("/api/reportes-diarios/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reporteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    // ===================== DELETE =====================

    @Test
    void eliminar_cuandoExiste_debeRetornar204() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/reportes-diarios/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_cuandoNoExiste_debeRetornar500() throws Exception {
        doThrow(new RuntimeException("Reporte no encontrado")).when(service).eliminar(99L);

        assertThrows(Exception.class, () ->
                mockMvc.perform(delete("/api/reportes-diarios/99").with(csrf())).andReturn()
        );
    }

    // ===================== CONSULTAS ESPECIALIZADAS =====================

    @Test
    void porRango_debeRetornarReportesFiltrados() throws Exception {
        when(service.buscarPorRangoFechas(any(), any())).thenReturn(Arrays.asList(reporteResponse));

        mockMvc.perform(get("/api/reportes-diarios/rango")
                        .param("inicio", "2025-01-01T00:00:00")
                        .param("fin", "2025-12-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void porEmpleado_debeRetornarReportesDelEmpleado() throws Exception {
        when(service.buscarPorEmpleado(1L)).thenReturn(Arrays.asList(reporteResponse));

        mockMvc.perform(get("/api/reportes-diarios/empleado/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void porTarea_debeRetornarReportesDeLaTarea() throws Exception {
        when(service.buscarPorTarea(1L)).thenReturn(Arrays.asList(reporteResponse));

        mockMvc.perform(get("/api/reportes-diarios/tarea/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void porEstado_debeRetornarFiltrado() throws Exception {
        when(service.listarPorEstado("PENDIENTE")).thenReturn(Arrays.asList(reporteResponse));

        mockMvc.perform(get("/api/reportes-diarios/estado/PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void bajoAvance_debeRetornarReportesCriticos() throws Exception {
        when(service.reportesBajoAvance()).thenReturn(Arrays.asList(reporteResponse));

        mockMvc.perform(get("/api/reportes-diarios/bajo-avance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void reportesDeHoy_debeRetornarReportesDelDia() throws Exception {
        when(service.reportesDeHoy()).thenReturn(Arrays.asList(reporteResponse));

        mockMvc.perform(get("/api/reportes-diarios/hoy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void reportesPorEmpleado_debeRetornarEstadisticas() throws Exception {
        when(service.reportesPorEmpleado()).thenReturn(Collections.singletonList(new Object[]{1L, 5L}));

        mockMvc.perform(get("/api/reportes-diarios/reportes-por-empleado"))
                .andExpect(status().isOk());
    }

    @Test
    void avancePromedio_debeRetornarEstadisticas() throws Exception {
        when(service.avancePromedioPorTarea()).thenReturn(Collections.singletonList(new Object[]{1L, 78.5}));

        mockMvc.perform(get("/api/reportes-diarios/avance-promedio"))
                .andExpect(status().isOk());
    }
}
