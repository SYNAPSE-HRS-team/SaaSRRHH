package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.DTO.UsuarioRequestDTO;
import com.SaasRRHH.main.DTO.UsuarioResponseDTO;
import com.SaasRRHH.main.services.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.SaasRRHH.main.security.JwtUtil;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService service;

        @MockBean
        private JwtUtil jwtUtil;

        @MockBean
        private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private UsuarioResponseDTO usuarioResponse;
    private UsuarioRequestDTO usuarioRequest;

    @BeforeEach
    void setUp() {
        usuarioResponse = new UsuarioResponseDTO();
        usuarioResponse.setId(1L);
        usuarioResponse.setEmail("admin@test.com");
        usuarioResponse.setRolId(1L);
        usuarioResponse.setRolNombre("ADMIN");
        usuarioResponse.setActivo(true);
        usuarioResponse.setFechaCreacion(LocalDateTime.now());

        usuarioRequest = new UsuarioRequestDTO();
        usuarioRequest.setEmail("admin@test.com");
        usuarioRequest.setPassword("password");
        usuarioRequest.setRolId(1L);
        usuarioRequest.setActivo(true);
    }

    // ===================== GET ALL =====================

    @Test
    void listar_debeRetornarListaDeUsuarios() throws Exception {
        when(service.listar()).thenReturn(Arrays.asList(usuarioResponse));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].email", is("admin@test.com")));
    }

    @Test
    void listar_cuandoVacia_debeRetornarListaVacia() throws Exception {
        when(service.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ===================== GET BY ID =====================

    @Test
    void obtener_cuandoExiste_debeRetornar200() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(usuarioResponse);

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void obtener_cuandoNoExiste_debeRetornar404() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new RuntimeException("Usuario no encontrado"));

        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    // ===================== POST =====================

    @Test
    void crear_conDatosValidos_debeRetornar201() throws Exception {
        when(service.guardar(any(UsuarioRequestDTO.class))).thenReturn(usuarioResponse);

        mockMvc.perform(post("/api/usuarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void crear_cuandoEmailDuplicado_debeRetornar400() throws Exception {
        when(service.guardar(any(UsuarioRequestDTO.class)))
                .thenThrow(new RuntimeException("El email ya está registrado"));

        mockMvc.perform(post("/api/usuarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequest)))
                .andExpect(status().isBadRequest());
    }

    // ===================== DELETE =====================

    @Test
    void eliminar_cuandoExiste_debeRetornar204() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/usuarios/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_cuandoNoExiste_debeRetornar404() throws Exception {
        doThrow(new RuntimeException("Usuario no encontrado")).when(service).eliminar(99L);

        mockMvc.perform(delete("/api/usuarios/99").with(csrf()))
                .andExpect(status().isNotFound());
    }

    // ===================== GET BY EMAIL =====================

    @Test
    void buscarPorEmail_cuandoExiste_debeRetornar200() throws Exception {
        when(service.buscarPorEmail("admin@test.com")).thenReturn(usuarioResponse);

        mockMvc.perform(get("/api/usuarios/email/admin@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("admin@test.com")));
    }

    @Test
    void buscarPorEmail_cuandoNoExiste_debeRetornar404() throws Exception {
        when(service.buscarPorEmail("noexiste@empresa.com"))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        mockMvc.perform(get("/api/usuarios/email/noexiste@empresa.com"))
                .andExpect(status().isNotFound());
    }

    // ===================== PATCH ULTIMO ACCESO =====================

    @Test
    void registrarUltimoAcceso_cuandoExiste_debeRetornar200() throws Exception {
        when(service.actualizarUltimoAcceso(1L)).thenReturn(usuarioResponse);

        mockMvc.perform(patch("/api/usuarios/1/ultimo-acceso").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void registrarUltimoAcceso_cuandoNoExiste_debeRetornar404() throws Exception {
        when(service.actualizarUltimoAcceso(99L)).thenThrow(new RuntimeException("Usuario no encontrado"));

        mockMvc.perform(patch("/api/usuarios/99/ultimo-acceso").with(csrf()))
                .andExpect(status().isNotFound());
    }

    // ===================== CONSULTAS JPQL =====================

    @Test
    void listarActivos_debeRetornarSoloActivos() throws Exception {
        when(service.listarUsuariosActivos()).thenReturn(Arrays.asList(usuarioResponse));

        mockMvc.perform(get("/api/usuarios/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void buscarPorRol_debeRetornarFiltrado() throws Exception {
        when(service.buscarPorRol("ADMIN")).thenReturn(Arrays.asList(usuarioResponse));

        mockMvc.perform(get("/api/usuarios/rol/ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void accesoReciente_debeRetornarLista() throws Exception {
        when(service.usuariosConAccesoReciente(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(usuarioResponse));

        mockMvc.perform(get("/api/usuarios/acceso-reciente")
                        .param("fecha", "2025-01-01T00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

        @Test
        void contarUsuariosPorRol_debeRetornarEstadisticas() throws Exception {
                when(service.contarUsuariosPorRol()).thenReturn(Collections.singletonList(new Object[]{"ADMIN", 3L}));

                mockMvc.perform(get("/api/usuarios/estadisticas/roles"))
                                .andExpect(status().isOk());
        }
}
