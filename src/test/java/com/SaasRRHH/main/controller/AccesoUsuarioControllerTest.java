package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.AccesoUsuario;
import com.SaasRRHH.main.services.AccesoUsuarioService;
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
class AccesoUsuarioControllerTest {

    @Mock
    private AccesoUsuarioService service;

    @InjectMocks
    private AccesoUsuarioController controller;

    private AccesoUsuario acceso;

    @BeforeEach
    void setUp() {
        acceso = new AccesoUsuario();
    }

    @Test
    void listar_debeRetornarOk() {
        when(service.listar()).thenReturn(List.of(acceso));

        ResponseEntity<List<AccesoUsuario>> response = controller.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(service).listar();
    }

    @Test
    void buscarPorId_debeRetornarOk() {
        when(service.buscarPorId(1L)).thenReturn(acceso);

        ResponseEntity<AccesoUsuario> response = controller.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(acceso, response.getBody());
        verify(service).buscarPorId(1L);
    }

    @Test
    void porUsuario_debeRetornarLista() {
        when(service.buscarPorUsuario(9L)).thenReturn(List.of(acceso));

        ResponseEntity<List<AccesoUsuario>> response = controller.porUsuario(9L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(service).buscarPorUsuario(9L);
    }

    @Test
    void guardar_debeRetornarCreated() {
        when(service.guardar(acceso)).thenReturn(acceso);

        ResponseEntity<AccesoUsuario> response = controller.guardar(acceso);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(acceso, response.getBody());
        verify(service).guardar(acceso);
    }

    @Test
    void actualizar_debeRetornarOk() {
        when(service.actualizar(1L, acceso)).thenReturn(acceso);

        ResponseEntity<AccesoUsuario> response = controller.actualizar(1L, acceso);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(acceso, response.getBody());
        verify(service).actualizar(1L, acceso);
    }

    @Test
    void eliminar_debeRetornarNoContent() {
        ResponseEntity<Void> response = controller.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).eliminar(1L);
    }
}
