package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.RegistroAsistenciaRequestDTO;
import com.SaasRRHH.main.DTO.RegistroAsistenciaResponseDTO;
import com.SaasRRHH.main.security.JwtUtil;
import com.SaasRRHH.main.services.RegistroAsistenciaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistroAsistenciaController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class RegistroAsistenciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistroAsistenciaService service;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private RegistroAsistenciaResponseDTO asistenciaResponse;
    private RegistroAsistenciaRequestDTO asistenciaRequest;

    @BeforeEach
    void setUp() {
        asistenciaResponse = new RegistroAsistenciaResponseDTO();
        asistenciaResponse.setId(1L);
        asistenciaResponse.setEmpleadoId(1L);
        asistenciaResponse.setFechaHora(LocalDateTime.now());
        asistenciaResponse.setTipoMarcacion("ENTRADA");
        asistenciaResponse.setMetodo("QR");
        asistenciaResponse.setEstado("VALIDADO");

        asistenciaRequest = new RegistroAsistenciaRequestDTO();
        asistenciaRequest.setEmpleadoId(1L);
        asistenciaRequest.setFechaHora(LocalDateTime.now());
        asistenciaRequest.setTipoMarcacion("ENTRADA");
        asistenciaRequest.setMetodo("QR");
    }

    @Test
    void listar_debeRetornarListaDeAsistencias() throws Exception {
        when(service.listar()).thenReturn(Arrays.asList(asistenciaResponse));

        mockMvc.perform(get("/api/asistencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void buscarPorId_cuandoExiste_debeRetornar200() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(asistenciaResponse);

        mockMvc.perform(get("/api/asistencias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void guardar_conDatosValidos_debeRetornar201() throws Exception {
        when(service.guardar(org.mockito.ArgumentMatchers.any(RegistroAsistenciaRequestDTO.class))).thenReturn(asistenciaResponse);

        mockMvc.perform(post("/api/asistencias")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(asistenciaRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void eliminar_cuandoExiste_debeRetornar204() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/asistencias/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void registrarEntrada_debeRetornar200() throws Exception {
        when(service.registrarEntrada(eq(1L), org.mockito.ArgumentMatchers.any())).thenReturn(asistenciaResponse);

        mockMvc.perform(post("/api/asistencias/entrada/1")
                .with(csrf())
                .param("metodo", "QR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoMarcacion", is("ENTRADA")));
    }

    @Test
    void registrarEntrada_cuandoYaMarco_debeRetornar500() throws Exception {
        when(service.registrarEntrada(eq(1L), any()))
            .thenThrow(new RuntimeException("El empleado ya registró entrada hoy"));

        mockMvc.perform(post("/api/asistencias/entrada/1").with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void registrarSalida_debeRetornar200() throws Exception {
        asistenciaResponse.setTipoMarcacion("SALIDA");
        when(service.registrarSalida(eq(1L), any())).thenReturn(asistenciaResponse);

        mockMvc.perform(post("/api/asistencias/salida/1")
                .with(csrf())
                .param("metodo", "QR"))
                .andExpect(status().isOk());
    }

    @Test
    void buscarPorEmpleado_debeRetornarAsistenciasDelEmpleado() throws Exception {
        when(service.buscarPorEmpleado(1L)).thenReturn(Arrays.asList(asistenciaResponse));

        mockMvc.perform(get("/api/asistencias/empleado/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void buscarPorEmpleadoYFecha_debeRetornarFiltrado() throws Exception {
        when(service.buscarPorEmpleadoYFecha(eq(1L), org.mockito.ArgumentMatchers.any())).thenReturn(Arrays.asList(asistenciaResponse));

        mockMvc.perform(get("/api/asistencias/empleado/1/fecha")
                        .param("fecha", "2025-05-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void buscarPorEstado_debeRetornarFiltrado() throws Exception {
        when(service.buscarPorEstado("VALIDADO")).thenReturn(Arrays.asList(asistenciaResponse));

        mockMvc.perform(get("/api/asistencias/estado/VALIDADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void asistenciasHoy_debeRetornarAsistenciasDelDia() throws Exception {
        when(service.asistenciasHoy()).thenReturn(Arrays.asList(asistenciaResponse));

        mockMvc.perform(get("/api/asistencias/hoy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void incidenciasAsistencia_debeRetornarIncidencias() throws Exception {
        when(service.incidenciasAsistencia()).thenReturn(Arrays.asList(asistenciaResponse));

        mockMvc.perform(get("/api/asistencias/incidencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void contarAsistenciasMensuales_debeRetornarConteo() throws Exception {
        when(service.contarAsistenciasMensuales(eq(1L), any(), any())).thenReturn(20L);

        mockMvc.perform(get("/api/asistencias/mensual/1")
                        .param("inicio", "2025-05-01T00:00:00")
                        .param("fin", "2025-05-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(content().string("20"));
    }

    @Test
    void yaMarcoHoy_debeRetornarBoolean() throws Exception {
        when(service.yaMarcoHoy(1L, "ENTRADA")).thenReturn(true);

        mockMvc.perform(get("/api/asistencias/ya-marco")
                        .param("empleadoId", "1")
                        .param("tipo", "ENTRADA"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void rankingTardanzas_debeRetornarRanking() throws Exception {
        when(service.rankingTardanzas()).thenReturn(Collections.singletonList(new Object[]{1L, "Juan", 3L}));

        mockMvc.perform(get("/api/asistencias/ranking-tardanzas"))
                .andExpect(status().isOk());
    }

    @Test
    void miQr_debeRetornarAsistenciaQr() throws Exception {
        com.SaasRRHH.main.DTO.AsistenciaQrDTO qrDTO = new com.SaasRRHH.main.DTO.AsistenciaQrDTO("payload", 1L, "Juan Perez", 30, 123456789L);
        when(service.generarQrEmpleadoActual()).thenReturn(qrDTO);

        mockMvc.perform(get("/api/asistencias/mi-qr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload", is("payload")))
                .andExpect(jsonPath("$.empleadoId", is(1)));
    }

    @Test
    void scanQr_debeRetornarRegistroAsistencia() throws Exception {
        when(service.registrarPorQr("payload")).thenReturn(asistenciaResponse);
        com.SaasRRHH.main.DTO.AsistenciaScanRequestDTO req = new com.SaasRRHH.main.DTO.AsistenciaScanRequestDTO();
        req.setPayload("payload");

        mockMvc.perform(post("/api/asistencias/scan-qr")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void miCalendario_debeRetornarCalendarioMes() throws Exception {
        com.SaasRRHH.main.DTO.AsistenciaCalendarioMesDTO mesDTO = new com.SaasRRHH.main.DTO.AsistenciaCalendarioMesDTO(2025, 5, Collections.emptyList());
        when(service.calendarioEmpleadoActual(2025, 5)).thenReturn(mesDTO);

        mockMvc.perform(get("/api/asistencias/mi-calendario")
                        .param("anio", "2025")
                        .param("mes", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.anio", is(2025)))
                .andExpect(jsonPath("$.mes", is(5)));
    }

    @Test
    void miCalendarioAnual_debeRetornarCalendarioAnual() throws Exception {
        com.SaasRRHH.main.DTO.AsistenciaCalendarioAnualDTO anualDTO = new com.SaasRRHH.main.DTO.AsistenciaCalendarioAnualDTO(2025, Collections.emptyList());
        when(service.calendarioAnualEmpleadoActual(2025)).thenReturn(anualDTO);

        mockMvc.perform(get("/api/asistencias/mi-calendario/anual")
                        .param("anio", "2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.anio", is(2025)));
    }

    @Test
    void calendarioEmpleado_debeRetornarCalendarioMes() throws Exception {
        com.SaasRRHH.main.DTO.AsistenciaCalendarioMesDTO mesDTO = new com.SaasRRHH.main.DTO.AsistenciaCalendarioMesDTO(2025, 5, Collections.emptyList());
        when(service.calendarioEmpleado(1L, 2025, 5)).thenReturn(mesDTO);

        mockMvc.perform(get("/api/asistencias/calendario/1")
                        .param("anio", "2025")
                        .param("mes", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.anio", is(2025)))
                .andExpect(jsonPath("$.mes", is(5)));
    }

    @Test
    void calendarioAnualEmpleado_debeRetornarCalendarioAnual() throws Exception {
        com.SaasRRHH.main.DTO.AsistenciaCalendarioAnualDTO anualDTO = new com.SaasRRHH.main.DTO.AsistenciaCalendarioAnualDTO(2025, Collections.emptyList());
        when(service.calendarioAnualEmpleado(1L, 2025)).thenReturn(anualDTO);

        mockMvc.perform(get("/api/asistencias/calendario/1/anual")
                        .param("anio", "2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.anio", is(2025)));
    }
}