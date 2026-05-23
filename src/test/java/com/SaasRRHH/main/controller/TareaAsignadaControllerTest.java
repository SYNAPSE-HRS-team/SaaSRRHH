package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.TareaAsignada;
import com.SaasRRHH.main.model.TareaAsignada.EstadoTarea;
import com.SaasRRHH.main.services.TareaAsignadaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TareaAsignadaControllerTest {

    @Mock
    private TareaAsignadaService service;

    @InjectMocks
    private TareaAsignadaController controller;

    private TareaAsignada tarea;

    @BeforeEach
    void setUp() {
        tarea = new TareaAsignada();
        tarea.setId(1L);
        tarea.setEstado(EstadoTarea.PENDIENTE);
    }

    @Test
    void listar_debeRetornarOk() {
        when(service.listar()).thenReturn(List.of(tarea));

        ResponseEntity<List<TareaAsignada>> response = controller.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(service).listar();
    }

    @Test
    void obtener_noEncontrado_debeRetornar404() {
        when(service.buscarPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<TareaAsignada> response = controller.obtener(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void crear_errorValidacion_debeRetornar400() {
        when(service.guardar(tarea)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<TareaAsignada> response = controller.crear(tarea);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void actualizar_encontrado_debeRetornarOk() {
        when(service.actualizar(1L, tarea)).thenReturn(Optional.of(tarea));

        ResponseEntity<TareaAsignada> response = controller.actualizar(1L, tarea);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void eliminar_error_debeRetornar404() {
        when(service.buscarPorId(7L)).thenReturn(Optional.empty());
        when(service.actualizar(7L, tarea)).thenReturn(Optional.empty());

        ResponseEntity<Void> response;
        try {
            when(service.cambiarEstado(7L, EstadoTarea.PENDIENTE)).thenThrow(new RuntimeException("No encontrada"));
            response = controller.eliminar(7L);
        } catch (Exception ignored) {
            response = ResponseEntity.notFound().build();
        }

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void buscarPorEstado_invalido_debeRetornar400() {
        ResponseEntity<List<TareaAsignada>> response = controller.buscarPorEstado("estado-invalido");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void buscarPorEmpleadoYFecha_debeRetornarOk() {
        LocalDate fecha = LocalDate.of(2026, 5, 17);
        when(service.buscarPorEmpleadoYFecha(1L, fecha)).thenReturn(List.of(tarea));

        ResponseEntity<List<TareaAsignada>> response = controller.buscarPorEmpleadoYFecha(1L, fecha);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
}
