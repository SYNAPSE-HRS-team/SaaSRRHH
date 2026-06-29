package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.Rol;
import com.SaasRRHH.main.services.RolService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RolController.class)
@WithMockUser
class RolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RolService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Rol rol;

    @BeforeEach
    void setUp() {
        rol = new Rol();
        rol.setIdRol(1L);
        rol.setNombreRol("ADMIN");
        rol.setDescripcion("Administrador del sistema");
    }

    // ===================== GET ALL =====================

    @Test
    void listar_debeRetornarListaDeRoles() throws Exception {
        when(service.listar()).thenReturn(Arrays.asList(rol));

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idRol", is(1)))
                .andExpect(jsonPath("$[0].nombreRol", is("ADMIN")));

        verify(service).listar();
    }

    @Test
    void listar_cuandoListaVacia_debeRetornarListaVacia() throws Exception {
        when(service.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ===================== GET BY ID =====================

    @Test
    void obtener_cuandoExiste_debeRetornar200() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(rol));

        mockMvc.perform(get("/api/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idRol", is(1)))
                .andExpect(jsonPath("$.nombreRol", is("ADMIN")));
    }

    @Test
    void obtener_cuandoNoExiste_debeRetornar404() throws Exception {
        when(service.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/roles/99"))
                .andExpect(status().isNotFound());
    }

    // ===================== POST =====================

    @Test
    void crear_conDatosValidos_debeRetornar201() throws Exception {
        when(service.guardar(any(Rol.class))).thenReturn(rol);

        mockMvc.perform(post("/api/roles").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rol)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idRol", is(1)))
                .andExpect(jsonPath("$.nombreRol", is("ADMIN")));
    }

    @Test
    void crear_cuandoNombreRepetido_debeRetornar400() throws Exception {
        when(service.guardar(any(Rol.class)))
                .thenThrow(new RuntimeException("Ya existe un rol con ese nombre"));

        mockMvc.perform(post("/api/roles").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rol)))
                .andExpect(status().isBadRequest());
    }

    // ===================== PUT =====================

    @Test
    void actualizar_cuandoExiste_debeRetornar200() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(rol));
        when(service.guardar(any(Rol.class))).thenReturn(rol);

        mockMvc.perform(put("/api/roles/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rol)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreRol", is("ADMIN")));
    }

    @Test
    void actualizar_cuandoNoExiste_debeRetornar404() throws Exception {
        when(service.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/roles/99").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rol)))
                .andExpect(status().isNotFound());
    }

    // ===================== DELETE =====================

    @Test
    void eliminar_cuandoExiste_debeRetornar204() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/roles/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_cuandoNoExiste_debeRetornar404() throws Exception {
        doThrow(new RuntimeException("Rol no encontrado"))
                .when(service).eliminar(99L);

        mockMvc.perform(delete("/api/roles/99").with(csrf()))
                .andExpect(status().isNotFound());
    }

    // ===================== BUSCAR POR NOMBRE =====================

    @Test
    void buscarPorNombre_cuandoExiste_debeRetornar200() throws Exception {
        when(service.buscarPorNombre("ADMIN")).thenReturn(Optional.of(rol));

        mockMvc.perform(get("/api/roles/buscar")
                        .param("nombre", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreRol", is("ADMIN")));
    }

    @Test
    void buscarPorNombre_cuandoNoExiste_debeRetornar404() throws Exception {
        when(service.buscarPorNombre("INEXISTENTE")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/roles/buscar")
                        .param("nombre", "INEXISTENTE"))
                .andExpect(status().isNotFound());
    }
}
