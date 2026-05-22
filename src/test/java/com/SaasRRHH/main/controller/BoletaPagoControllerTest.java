package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.BoletaPago;
import com.SaasRRHH.main.services.BoletaPagoService;
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
class BoletaPagoControllerTest {

    @Mock
    private BoletaPagoService service;

    @InjectMocks
    private BoletaPagoController controller;

    private BoletaPago boleta;

    @BeforeEach
    void setUp() {
        boleta = new BoletaPago();
        boleta.setId(1L);
    }

    @Test
    void listar_debeRetornarLista() {
        when(service.listar()).thenReturn(List.of(boleta));

        List<BoletaPago> resultado = controller.listar();

        assertEquals(1, resultado.size());
        verify(service).listar();
    }

    @Test
    void buscar_encontrado_debeRetornarOk() {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(boleta));

        ResponseEntity<BoletaPago> response = controller.buscar(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(service).buscarPorId(1L);
    }

    @Test
    void buscar_noEncontrado_debeRetornar404() {
        when(service.buscarPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<BoletaPago> response = controller.buscar(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(service).buscarPorId(99L);
    }

    @Test
    void crear_debeRetornarEntidad() {
        when(service.guardar(boleta)).thenReturn(boleta);

        BoletaPago response = controller.crear(boleta);

        assertEquals(1L, response.getId());
        verify(service).guardar(boleta);
    }

    @Test
    void actualizar_encontrado_debeRetornarOk() {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(boleta));
        when(service.actualizar(1L, boleta)).thenReturn(boleta);

        ResponseEntity<BoletaPago> response = controller.actualizar(1L, boleta);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(service).buscarPorId(1L);
        verify(service).actualizar(1L, boleta);
    }

    @Test
    void eliminar_noEncontrado_debeRetornar404() {
        when(service.buscarPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = controller.eliminar(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(service).buscarPorId(99L);
    }
}
