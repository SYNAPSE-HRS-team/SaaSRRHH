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

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RolController.class)
class RolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RolService rolService;

    @Autowired
    private ObjectMapper objectMapper;

    private Rol rolAdmin;
    private Rol rolSupervisor;

    @BeforeEach
    void setUp() {
        rolAdmin = new Rol();
        rolAdmin.setIdRol(1L);
        rolAdmin.setNombreRol("ADMIN");
        rolAdmin.setDescripcion("Administrador del sistema");

        rolSupervisor = new Rol();
        rolSupervisor.setIdRol(2L);
        rolSupervisor.setNombreRol("SUPERVISOR");
        rolSupervisor.setDescripcion("Supervisor de área");
    }

    @Test
    void testListarRoles_Exitoso() throws Exception {
        when(rolService.listar()).thenReturn(Arrays.asList(rolAdmin, rolSupervisor));

        mockMvc.perform(get("/api/roles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombreRol").value("ADMIN"))
                .andExpect(jsonPath("$[1].nombreRol").value("SUPERVISOR"));

        verify(rolService, times(1)).listar();
    }

    @Test
    void testObtenerRolPorId_Encontrado() throws Exception {
        when(rolService.buscarPorId(1L)).thenReturn(Optional.of(rolAdmin));

        mockMvc.perform(get("/api/roles/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idRol").value(1))
                .andExpect(jsonPath("$.nombreRol").value("ADMIN"));

        verify(rolService, times(1)).buscarPorId(1L);
    }

    @Test
    void testObtenerRolPorId_NoEncontrado() throws Exception {
        when(rolService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/roles/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(rolService, times(1)).buscarPorId(99L);
    }

    @Test
    void testCrearRol_Exitoso() throws Exception {
        Rol nuevoRol = new Rol();
        nuevoRol.setNombreRol("GERENTE");
        nuevoRol.setDescripcion("Gerente general");

        Rol rolCreado = new Rol();
        rolCreado.setIdRol(4L);
        rolCreado.setNombreRol("GERENTE");
        rolCreado.setDescripcion("Gerente general");

        when(rolService.guardar(any(Rol.class))).thenReturn(rolCreado);

        mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoRol)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idRol").value(4))
                .andExpect(jsonPath("$.nombreRol").value("GERENTE"));

        verify(rolService, times(1)).guardar(any(Rol.class));
    }

    @Test
    void testCrearRol_NombreDuplicado_RetornaBadRequest() throws Exception {
        Rol rolDuplicado = new Rol();
        rolDuplicado.setNombreRol("ADMIN");

        when(rolService.guardar(any(Rol.class))).thenThrow(new RuntimeException("Ya existe un rol"));

        mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rolDuplicado)))
                .andExpect(status().isBadRequest());

        verify(rolService, times(1)).guardar(any(Rol.class));
    }

    @Test
    void testActualizarRol_Exitoso() throws Exception {
        Rol rolActualizado = new Rol();
        rolActualizado.setNombreRol("ADMIN");
        rolActualizado.setDescripcion("Administrador - Actualizado");

        Rol rolActualizadoConId = new Rol();
        rolActualizadoConId.setIdRol(1L);
        rolActualizadoConId.setNombreRol("ADMIN");
        rolActualizadoConId.setDescripcion("Administrador - Actualizado");

        when(rolService.buscarPorId(1L)).thenReturn(Optional.of(rolAdmin));
        when(rolService.guardar(any(Rol.class))).thenReturn(rolActualizadoConId);

        mockMvc.perform(put("/api/roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rolActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion").value("Administrador - Actualizado"));

        verify(rolService, times(1)).buscarPorId(1L);
        verify(rolService, times(1)).guardar(any(Rol.class));
    }

    @Test
    void testActualizarRol_NoEncontrado_RetornaNotFound() throws Exception {
        Rol rolActualizado = new Rol();
        rolActualizado.setNombreRol("ADMIN");

        when(rolService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/roles/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rolActualizado)))
                .andExpect(status().isNotFound());

        verify(rolService, times(1)).buscarPorId(99L);
        verify(rolService, never()).guardar(any(Rol.class));
    }

    @Test
    void testEliminarRol_Exitoso() throws Exception {
        doNothing().when(rolService).eliminar(1L);

        mockMvc.perform(delete("/api/roles/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(rolService, times(1)).eliminar(1L);
    }

    @Test
    void testEliminarRol_NoEncontrado_RetornaNotFound() throws Exception {
        doThrow(new RuntimeException("Rol no encontrado")).when(rolService).eliminar(99L);

        mockMvc.perform(delete("/api/roles/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(rolService, times(1)).eliminar(99L);
    }

    @Test
    void testBuscarRolPorNombre_Encontrado() throws Exception {
        when(rolService.buscarPorNombre("ADMIN")).thenReturn(Optional.of(rolAdmin));

        mockMvc.perform(get("/api/roles/buscar")
                .param("nombre", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreRol").value("ADMIN"));

        verify(rolService, times(1)).buscarPorNombre("ADMIN");
    }

    @Test
    void testBuscarRolPorNombre_NoEncontrado_RetornaNotFound() throws Exception {
        when(rolService.buscarPorNombre("INEXISTENTE")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/roles/buscar")
                .param("nombre", "INEXISTENTE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(rolService, times(1)).buscarPorNombre("INEXISTENTE");
    }
}