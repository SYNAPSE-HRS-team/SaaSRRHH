package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.FeedbackAnonimoRequestDTO;
import com.SaasRRHH.main.DTO.FeedbackAnonimoResponseDTO;
import com.SaasRRHH.main.model.FeedbackAnonimo;
import com.SaasRRHH.main.services.FeedbackAnonimoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FeedbackAnonimoController.class)
@AutoConfigureMockMvc(addFilters = false)
class FeedbackAnonimoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeedbackAnonimoService service;

    @Autowired
    private ObjectMapper objectMapper;

    private FeedbackAnonimoResponseDTO feedbackResponse;
    private FeedbackAnonimoRequestDTO feedbackRequest;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        feedbackResponse = new FeedbackAnonimoResponseDTO();
        feedbackResponse.setId(1L);
        feedbackResponse.setMensaje("El clima laboral es tenso");
        feedbackResponse.setCategoria("CLIMA_LABORAL");
        feedbackResponse.setEstado("PENDIENTE");
        feedbackResponse.setFechaEnvio(LocalDateTime.now());
        feedbackResponse.setEmpleadoId(1L);
        feedbackResponse.setNombreEmpleado("Juan Pérez");
        feedbackResponse.setEsAnonimo(false);
        feedbackResponse.setRespuesta(null);
        feedbackResponse.setFechaRespuesta(null);

        feedbackRequest = new FeedbackAnonimoRequestDTO();
        feedbackRequest.setMensaje("El clima laboral es tenso");
        feedbackRequest.setCategoria("CLIMA_LABORAL");
        feedbackRequest.setEmpleadoId(1L);
        feedbackRequest.setEsAnonimo(false);
    }

    @Test
    void enviar_conDatosValidos_debeRetornar201() throws Exception {
        when(service.enviarFeedback(any(FeedbackAnonimoRequestDTO.class))).thenReturn(feedbackResponse);

        mockMvc.perform(post("/api/feedback-anonimo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.mensaje").value("El clima laboral es tenso"));
    }

    @Test
    void enviar_cuandoMensajeVacio_debeRetornar400() throws Exception {
        when(service.enviarFeedback(any()))
                .thenThrow(new IllegalArgumentException("Mensaje es requerido"));

        FeedbackAnonimoRequestDTO requestSinMensaje = new FeedbackAnonimoRequestDTO();
        requestSinMensaje.setCategoria("CLIMA_LABORAL");

        mockMvc.perform(post("/api/feedback-anonimo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestSinMensaje)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listar_debeRetornarListaDeFeedbacks() throws Exception {
        when(service.listar()).thenReturn(Arrays.asList(feedbackResponse));

        mockMvc.perform(get("/api/feedback-anonimo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void porCategoria_debeRetornarFiltrado() throws Exception {
        when(service.listarPorCategoria(FeedbackAnonimo.CategoriaFeedback.CLIMA_LABORAL))
                .thenReturn(Arrays.asList(feedbackResponse));

        mockMvc.perform(get("/api/feedback-anonimo/categoria/CLIMA_LABORAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void porEstado_debeRetornarFiltrado() throws Exception {
        when(service.listarPorEstado(FeedbackAnonimo.EstadoFeedback.PENDIENTE))
                .thenReturn(Arrays.asList(feedbackResponse));

        mockMvc.perform(get("/api/feedback-anonimo/estado/PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void cambiarEstado_cuandoExiste_debeRetornar200() throws Exception {
        feedbackResponse.setEstado("REVISADO");
        when(service.cambiarEstado(eq(1L), eq(FeedbackAnonimo.EstadoFeedback.REVISADO)))
                .thenReturn(feedbackResponse);

        mockMvc.perform(patch("/api/feedback-anonimo/1/estado")
                        .param("estado", "REVISADO"))
                .andExpect(status().isOk());
    }

    @Test
    void cambiarEstado_cuandoNoExiste_debeRetornar404() throws Exception {
        when(service.cambiarEstado(eq(99L), any()))
                .thenThrow(new IllegalArgumentException("Feedback no encontrado"));

        mockMvc.perform(patch("/api/feedback-anonimo/99/estado")
                        .param("estado", "REVISADO"))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_cuandoExiste_debeRetornar204() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/feedback-anonimo/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_cuandoNoExiste_debeRetornar404() throws Exception {
        doThrow(new IllegalArgumentException("Feedback no encontrado"))
                .when(service).eliminar(99L);

        mockMvc.perform(delete("/api/feedback-anonimo/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void responderFeedback_debeRetornar200() throws Exception {
        feedbackResponse.setEstado("REVISADO");
        feedbackResponse.setRespuesta("Gracias por tu feedback");
        feedbackResponse.setFechaRespuesta(LocalDateTime.now());

        when(service.responderFeedback(eq(1L), any(), eq(FeedbackAnonimo.EstadoFeedback.REVISADO)))
                .thenReturn(feedbackResponse);

        String body = "{\"respuesta\":\"Gracias por tu feedback\",\"estado\":\"REVISADO\"}";

        mockMvc.perform(post("/api/feedback-anonimo/1/responder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }
}