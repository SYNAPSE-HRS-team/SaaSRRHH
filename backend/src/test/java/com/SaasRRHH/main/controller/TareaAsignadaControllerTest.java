package com.SaasRRHH.main.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc // Esto es fundamental para probar controladores
public class TareaAsignadaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN") // Simulamos ser ADMIN
    void testObtenerSeguimientoArea_ComoAdmin_DebeFuncionar() throws Exception {
        mockMvc.perform(get("/api/tareas-asignadas/seguimiento/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO") // Simulamos ser EMPLEADO (debería ser bloqueado)
    void testObtenerSeguimientoArea_ComoEmpleado_DebeSerProhibido() throws Exception {
        mockMvc.perform(get("/api/tareas-asignadas/seguimiento/1"))
                .andExpect(status().isForbidden()); // Esperamos 403 Forbidden
    }

}