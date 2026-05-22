package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.Familiar;
import com.SaasRRHH.main.services.FamiliarService;
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
class FamiliarControllerTest {

    @Mock
    private FamiliarService service;

    @InjectMocks
    private FamiliarController controller;

    private Familiar familiar;

    @BeforeEach
    void setUp() {
        familiar = new Familiar();
        familiar.setId(1L);
    }

    @Test
    void listar_debeRetornarOk() {
        when(service.listar()).thenReturn(List.of(familiar));

        ResponseEntity<List<Familiar>> response = controller.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(service).listar();
    }

    @Test
    void buscarPorId_encontrado_debeRetornarOk() {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(familiar));

        ResponseEntity<Familiar> response = controller.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void buscarPorId_noEncontrado_debeRetornar404() {
        when(service.buscarPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<Familiar> response = controller.buscarPorId(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void guardar_debeRetornarCreated() {
        when(service.guardar(familiar)).thenReturn(familiar);

        ResponseEntity<Familiar> response = controller.guardar(familiar);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void eliminar_error_debeRetornar404() {
        when(service.buscarPorId(9L)).thenReturn(Optional.empty());
        when(service.actualizar(9L, familiar)).thenThrow(new RuntimeException("No encontrado"));

        ResponseEntity<Familiar> response = controller.actualizar(9L, familiar);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void eliminar_debeRetornarNoContent() {
        ResponseEntity<Void> response = controller.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).eliminar(1L);
    }
}
