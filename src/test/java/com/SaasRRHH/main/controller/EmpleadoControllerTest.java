package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.services.EmpleadoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EmpleadoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmpleadoService empleadoService;

    @InjectMocks
    private EmpleadoController empleadoController;

    private ObjectMapper objectMapper;
    private Empleado empleadoEjemplo;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(empleadoController).build();
        
        // ✅ Configuración correcta para LocalDateTime
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        empleadoEjemplo = new Empleado();
        empleadoEjemplo.setId(1L);
        empleadoEjemplo.setNombres("Juan");
        empleadoEjemplo.setApellidos("Perez");
        empleadoEjemplo.setDni("12345678");
        
        // Si tu modelo Empleado tiene estos campos, configúralos
        // empleadoEjemplo.setFechaRegistro(LocalDateTime.now());
        // empleadoEjemplo.setFechaInicioContrato(LocalDate.now());
    }

    @Test
    void testListar() throws Exception {
        when(empleadoService.listar()).thenReturn(Arrays.asList(empleadoEjemplo));

        mockMvc.perform(get("/api/empleados")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombres").value("Juan"))
                .andExpect(jsonPath("$[0].dni").value("12345678"));
    }

    @Test
    void testObtener() throws Exception {
        when(empleadoService.buscarPorId(1L)).thenReturn(Optional.of(empleadoEjemplo));

        mockMvc.perform(get("/api/empleados/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombres").value("Juan"));
    }

    @Test
    void testCrear() throws Exception {
        when(empleadoService.guardar(any(Empleado.class))).thenReturn(empleadoEjemplo);

        String empleadoJson = objectMapper.writeValueAsString(empleadoEjemplo);
        
        mockMvc.perform(post("/api/empleados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(empleadoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testEliminar() throws Exception {
        doNothing().when(empleadoService).eliminar(1L);

        mockMvc.perform(delete("/api/empleados/1"))
                .andExpect(status().isOk());
    }
}