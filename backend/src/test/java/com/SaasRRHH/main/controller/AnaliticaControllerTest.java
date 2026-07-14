package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.DashboardDTO;
import com.SaasRRHH.main.services.AnaliticaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnaliticaController.class)
@WithMockUser
class AnaliticaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnaliticaService analiticaService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void dashboard_debeRetornar200_yContenido() throws Exception {
        // ✅ Usar setters en lugar del constructor con 7 parámetros
        DashboardDTO dto = new DashboardDTO();
        dto.setTotalEmpleados(100L);
        dto.setTotalUsuarios(80L);
        dto.setReportesDiarios(120L);
        dto.setAusencias(5L);
        dto.setIncidentes(2L);
        dto.setPorcentajeAusentismo(3.5);
        dto.setNivelRiesgo("MEDIO");
        dto.setEmpleadosRiesgoAlto(0L);
        dto.setTotalAlertas(0L);
        dto.setPromedioPuntualidad(100.0);
        dto.setTotalFaltasHoy(0L);
        dto.setTotalTardanzasHoy(0L);
        dto.setFeedbackPendientes(0L);
        dto.setRankingBajoDesempeno(new ArrayList<>());
        
        when(analiticaService.obtenerDashboard()).thenReturn(dto);

        mockMvc.perform(get("/api/analitica/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmpleados").value(100))
                .andExpect(jsonPath("$.totalUsuarios").value(80))
                .andExpect(jsonPath("$.reportesDiarios").value(120))
                .andExpect(jsonPath("$.porcentajeAusentismo").value(3.5))
                .andExpect(jsonPath("$.nivelRiesgo").value("MEDIO"));
    }

    @Test
    void dashboard_servicioFalla_debeRetornar500() throws Exception {
        when(analiticaService.obtenerDashboard()).thenThrow(new RuntimeException("error"));

        mockMvc.perform(get("/api/analitica/dashboard"))
                .andExpect(status().isInternalServerError());
    }
}