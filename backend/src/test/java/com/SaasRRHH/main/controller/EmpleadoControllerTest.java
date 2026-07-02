package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.EmpleadoRequestDTO;
import com.SaasRRHH.main.DTO.EmpleadoResponseDTO;
import com.SaasRRHH.main.security.JwtUtil;
import com.SaasRRHH.main.services.EmpleadoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmpleadoController.class)
@WithMockUser
class EmpleadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpleadoService service;
            @MockBean
        private JwtUtil jwtUtil;

        @MockBean
        private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmpleadoResponseDTO empleadoResponse;
    private EmpleadoRequestDTO empleadoRequest;

    @BeforeEach
    void setUp() {
        empleadoResponse = new EmpleadoResponseDTO();
        empleadoResponse.setId(1L);
        empleadoResponse.setNombres("Juan");
        empleadoResponse.setApellidos("Pérez");
        empleadoResponse.setDni("12345678");
        empleadoResponse.setSueldoBase(new BigDecimal("1200.00"));
        empleadoResponse.setCargo("OPERARIO");
        empleadoResponse.setActivo(true);

        empleadoRequest = new EmpleadoRequestDTO();
        empleadoRequest.setUsuarioId(1L);
        empleadoRequest.setNombres("Juan");
        empleadoRequest.setApellidos("Pérez");
        empleadoRequest.setDni("12345678");
        empleadoRequest.setSueldoBase(new BigDecimal("1200.00"));
        empleadoRequest.setCargo("OPERARIO");
        empleadoRequest.setFechaInicioContrato(LocalDate.now());
        empleadoRequest.setActivo(true);
    }

    // ===================== GET ALL =====================

    @Test
    void listar_debeRetornarListaDeEmpleados() throws Exception {
        when(service.listar()).thenReturn(Arrays.asList(empleadoResponse));

        mockMvc.perform(get("/api/empleados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombres", is("Juan")));
    }

    @Test
    void listar_cuandoVacia_debeRetornarListaVacia() throws Exception {
        when(service.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/empleados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ===================== GET BY ID =====================

    @Test
    void obtener_cuandoExiste_debeRetornar200() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(empleadoResponse);

        mockMvc.perform(get("/api/empleados/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombres", is("Juan")));
    }

    @Test
    void obtener_cuandoNoExiste_debeRetornar404() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new RuntimeException("Empleado no encontrado"));

        mockMvc.perform(get("/api/empleados/99"))
                .andExpect(status().isNotFound());
    }

    // ===================== GET BY DNI =====================

    @Test
    void buscarPorDni_cuandoExiste_debeRetornar200() throws Exception {
        when(service.buscarPorDni("12345678")).thenReturn(empleadoResponse);

        mockMvc.perform(get("/api/empleados/dni/12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni", is("12345678")));
    }

    @Test
    void buscarPorDni_cuandoNoExiste_debeRetornar404() throws Exception {
        when(service.buscarPorDni("99999999")).thenThrow(new RuntimeException("Empleado no encontrado"));

        mockMvc.perform(get("/api/empleados/dni/99999999"))
                .andExpect(status().isNotFound());
    }

    // ===================== GET ACTIVOS =====================

    @Test
    void listarActivos_debeRetornarSoloActivos() throws Exception {
        when(service.listarActivos()).thenReturn(Arrays.asList(empleadoResponse));

        mockMvc.perform(get("/api/empleados/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].activo", is(true)));
    }

    // ===================== POST =====================

    @Test
    void crear_conDatosValidos_debeRetornar201() throws Exception {
        when(service.guardar(any(EmpleadoRequestDTO.class))).thenReturn(empleadoResponse);

        mockMvc.perform(post("/api/empleados")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empleadoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombres", is("Juan")));
    }

    @Test
    void crear_cuandoDniDuplicado_debeRetornar400() throws Exception {
        when(service.guardar(any(EmpleadoRequestDTO.class)))
                .thenThrow(new RuntimeException("El DNI ya está registrado"));

        mockMvc.perform(post("/api/empleados")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empleadoRequest)))
                .andExpect(status().isBadRequest());
    }

    // ===================== DELETE =====================

    @Test
    void eliminar_cuandoExiste_debeRetornar204() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/empleados/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_cuandoNoExiste_debeRetornar404() throws Exception {
        doThrow(new RuntimeException("Empleado no encontrado")).when(service).eliminar(99L);

        mockMvc.perform(delete("/api/empleados/99").with(csrf()))
                .andExpect(status().isNotFound());
    }

    // ===================== CONSULTAS JPQL =====================

    @Test
    void buscarPorCargo_debeRetornarEmpleadosFiltrados() throws Exception {
        when(service.buscarPorCargo("OPERARIO")).thenReturn(Arrays.asList(empleadoResponse));

        mockMvc.perform(get("/api/empleados/cargo/OPERARIO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void buscarPorCargoYActivo_debeRetornarFiltro() throws Exception {
        when(service.buscarPorCargoYActivo("OPERARIO", true)).thenReturn(Arrays.asList(empleadoResponse));

        mockMvc.perform(get("/api/empleados/cargo-activo")
                        .param("cargo", "OPERARIO")
                        .param("activo", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listarActivosConUsuario_debeRetornarLista() throws Exception {
        when(service.listarActivosConUsuario()).thenReturn(Arrays.asList(empleadoResponse));

        mockMvc.perform(get("/api/empleados/activos-usuario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void contratosVencidos_debeRetornarLista() throws Exception {
        when(service.contratosVencidos()).thenReturn(Arrays.asList(empleadoResponse));

        mockMvc.perform(get("/api/empleados/contratos-vencidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void contratosPorVencer_debeRetornarLista() throws Exception {
        when(service.contratosPorVencer(any(LocalDate.class))).thenReturn(Arrays.asList(empleadoResponse));

        mockMvc.perform(get("/api/empleados/contratos-por-vencer")
                        .param("fechaLimite", "2025-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void contarEmpleadosPorCargo_debeRetornarEstadisticas() throws Exception {
        when(service.contarEmpleadosPorCargo()).thenReturn(Collections.singletonList(new Object[]{"OPERARIO", 5L}));

        mockMvc.perform(get("/api/empleados/estadisticas/cargos"))
                .andExpect(status().isOk());
    }
}
