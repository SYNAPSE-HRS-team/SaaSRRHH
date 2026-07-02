package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.DashboardDTO;
import com.SaasRRHH.main.security.JwtUtil;
import com.SaasRRHH.main.services.AnaliticaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnaliticaController.class)
@WithMockUser
class AnaliticaControllerTest {

    @Autowired
    private org.springframework.test.web.servlet.MockMvc mockMvc;

    @MockBean
    private AnaliticaService analiticaService;
            @MockBean
        private JwtUtil jwtUtil;

        @MockBean
        private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void dashboard_debeRetornar200_yContenido() throws Exception {
        DashboardDTO dto = new DashboardDTO(100L, 80L, 120L, 5L, 2L, 3.5d, "MEDIO");
        when(analiticaService.obtenerDashboard()).thenReturn(dto);

        mockMvc.perform(get("/api/analitica/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmpleados", is(100)))
                .andExpect(jsonPath("$.totalUsuarios", is(80)))
                .andExpect(jsonPath("$.reportesDiarios", is(120)))
                .andExpect(jsonPath("$.porcentajeAusentismo", is(3.5)))
                .andExpect(jsonPath("$.nivelRiesgo", is("MEDIO")));
    }

    @Test
    void dashboard_servicioFalla_debeLanzarServletException() {
        when(analiticaService.obtenerDashboard()).thenThrow(new RuntimeException("error"));

        org.junit.jupiter.api.Assertions.assertThrows(jakarta.servlet.ServletException.class,
                () -> mockMvc.perform(get("/api/analitica/dashboard")).andReturn());
    }
}
