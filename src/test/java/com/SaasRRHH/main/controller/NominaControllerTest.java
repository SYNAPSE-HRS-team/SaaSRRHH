package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.BonoDescuento;
import com.SaasRRHH.main.model.Planilla;
import com.SaasRRHH.main.services.BonoDescuentoService;
import com.SaasRRHH.main.services.NominaProcessorService;
import com.SaasRRHH.main.services.PlanillaService;
import com.SaasRRHH.main.services.PdfGeneratorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NominaController.class)
@WithMockUser
class NominaControllerTest {

    @Autowired
    private org.springframework.test.web.servlet.MockMvc mockMvc;

    @MockBean
    private NominaProcessorService nominaProcessor;

    @MockBean
    private PlanillaService planillaService;

    @MockBean
    private BonoDescuentoService bonoService;

    @MockBean
    private PdfGeneratorService pdfGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    private Planilla ejemploPlanilla;
    private BonoDescuento ejemploBono;

    @BeforeEach
    void setUp() {
        ejemploPlanilla = new Planilla();
        ejemploPlanilla.setId(10L);
        ejemploPlanilla.setMes(1);
        ejemploPlanilla.setAnio(2025);

        ejemploBono = new BonoDescuento();
        ejemploBono.setId(5L);
        ejemploBono.setMotivo("Bono extraordinario");
        ejemploBono.setMonto(new BigDecimal("150.00"));
        ejemploBono.setMes(1);
        ejemploBono.setAnio(2025);
    }

    @Test
    void generarPlanilla_debeRetornar200_yJson() throws Exception {
        when(nominaProcessor.generarPlanilla(anyInt(), anyInt())).thenReturn(ejemploPlanilla);

        mockMvc.perform(post("/api/nomina/generar").param("mes", "1").param("anio", "2025").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.mes", is(1)))
                .andExpect(jsonPath("$.anio", is(2025)));
    }

    @Test
    void generarPlanilla_cuandoServicioFalla_debeRetornar5xx() throws Exception {
        when(nominaProcessor.generarPlanilla(anyInt(), anyInt())).thenThrow(new RuntimeException("fallo"));
        org.junit.jupiter.api.Assertions.assertThrows(jakarta.servlet.ServletException.class,
            () -> mockMvc.perform(post("/api/nomina/generar").param("mes", "1").param("anio", "2025").with(csrf())).andReturn());
    }

    @Test
    void listarPlanillas_debeRetornarLista() throws Exception {
        Planilla otra = new Planilla();
        otra.setId(11L);
        otra.setMes(2);
        otra.setAnio(2025);

        when(planillaService.listar()).thenReturn(Arrays.asList(ejemploPlanilla, otra));

        mockMvc.perform(get("/api/nomina/planillas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(10)))
                .andExpect(jsonPath("$[1].id", is(11)));
    }

    @Test
    void crearBono_debeRetornar200_yJson() throws Exception {
        when(bonoService.crear(org.mockito.ArgumentMatchers.any(BonoDescuento.class))).thenReturn(ejemploBono);

        // crear un JSON reducido que represente el bono
        String json = objectMapper.writeValueAsString(ejemploBono);

        mockMvc.perform(post("/api/nomina/bonos").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.motivo", is("Bono extraordinario")));
    }

    @Test
    void descargarBoletaPdf_debeRetornarPdfYHeaders() throws Exception {
        byte[] contenido = new byte[]{1, 2, 3, 4};
        when(pdfGenerator.generarBoletaPdf(1L)).thenReturn(contenido);

        mockMvc.perform(get("/api/nomina/boleta/1/pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", containsString("boleta-1.pdf")))
                .andExpect(content().bytes(contenido));
    }

}
