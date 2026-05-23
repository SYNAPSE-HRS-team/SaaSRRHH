package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.DispositivoAutorizado;
import com.SaasRRHH.main.services.DispositivoAutorizadoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DispositivoAutorizadoControllerTest {

    @Mock
    private DispositivoAutorizadoService service;

    @InjectMocks
    private DispositivoAutorizadoController controller;

    private DispositivoAutorizado dispositivo;

    @BeforeEach
    void setUp() {
        dispositivo = new DispositivoAutorizado();
        dispositivo.setId(1L);
    }

    @Test
    void listarTodos_debeRetornarOk() {
        when(service.listarTodo()).thenReturn(List.of(dispositivo));

        ResponseEntity<List<DispositivoAutorizado>> response = controller.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(service).listarTodo();
    }

    @Test
    void buscarPorId_encontrado_debeRetornarOk() {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(dispositivo));

        ResponseEntity<DispositivoAutorizado> response = controller.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void buscarPorId_noEncontrado_debeRetornar404() {
        when(service.buscarPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<DispositivoAutorizado> response = controller.buscarPorId(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void guardar_debeRetornarCreated() {
        when(service.guardar(dispositivo)).thenReturn(dispositivo);

        ResponseEntity<DispositivoAutorizado> response = controller.guardar(dispositivo);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void actualizar_error_debeRetornar404() {
        when(service.actualizar(99L, dispositivo)).thenThrow(new RuntimeException("No encontrado"));

        ResponseEntity<DispositivoAutorizado> response = controller.actualizar(99L, dispositivo);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void eliminar_debeRetornarNoContent() {
        ResponseEntity<Void> response = controller.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).eliminar(1L);
    }
}
