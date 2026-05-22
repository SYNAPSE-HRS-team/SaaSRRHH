package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.MetricaBurnout;
import com.SaasRRHH.main.services.MetricaBurnoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetricaBurnoutControllerTest {

    @Mock
    private MetricaBurnoutService service;

    @InjectMocks
    private MetricaBurnoutController controller;

    private MetricaBurnout metrica;

    @BeforeEach
    void setUp() {
        metrica = new MetricaBurnout();
        metrica.setId(1L);
    }

    @Test
    void listarMetricas_debeRetornarOk() {
        when(service.listar()).thenReturn(List.of(metrica));

        ResponseEntity<List<MetricaBurnout>> response = controller.listarMetricas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(service).listar();
    }

    @Test
    void obtenerPorId_debeRetornarOk() {
        when(service.obtenerPorId(1L)).thenReturn(metrica);

        ResponseEntity<MetricaBurnout> response = controller.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void buscarPorEmpleado_debeRetornarLista() {
        when(service.buscarPorEmpleado(10L)).thenReturn(List.of(metrica));

        ResponseEntity<List<MetricaBurnout>> response = controller.buscarPorEmpleado(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void crearMetrica_debeRetornarCreated() {
        when(service.guardar(metrica)).thenReturn(metrica);

        ResponseEntity<MetricaBurnout> response = controller.crearMetrica(metrica);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void eliminar_debeRetornarNoContent() {
        ResponseEntity<Void> response = controller.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).eliminar(1L);
    }
}
