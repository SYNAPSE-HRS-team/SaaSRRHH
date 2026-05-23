package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.TipoDocumento;
import com.SaasRRHH.main.services.TipoDocumentoService;
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
class TipoDocumentoControllerTest {

    @Mock
    private TipoDocumentoService service;

    @InjectMocks
    private TipoDocumentoController controller;

    private TipoDocumento tipo;

    @BeforeEach
    void setUp() {
        tipo = new TipoDocumento();
    }

    @Test
    void listar_debeRetornarOk() {
        when(service.listar()).thenReturn(List.of(tipo));

        ResponseEntity<List<TipoDocumento>> response = controller.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(service).listar();
    }

    @Test
    void buscarPorId_encontrado_debeRetornarOk() {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(tipo));

        ResponseEntity<TipoDocumento> response = controller.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tipo, response.getBody());
    }

    @Test
    void buscarPorId_noEncontrado_debeRetornar404() {
        when(service.buscarPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<TipoDocumento> response = controller.buscarPorId(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void guardar_debeRetornarCreated() {
        when(service.guardar(tipo)).thenReturn(tipo);

        ResponseEntity<TipoDocumento> response = controller.guardar(tipo);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(tipo, response.getBody());
    }

    @Test
    void actualizar_error_debeRetornar404() {
        when(service.actualizar(8L, tipo)).thenThrow(new RuntimeException("No encontrado"));

        ResponseEntity<TipoDocumento> response = controller.actualizar(8L, tipo);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void eliminar_debeRetornarNoContent() {
        ResponseEntity<Void> response = controller.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).eliminar(1L);
    }
}
