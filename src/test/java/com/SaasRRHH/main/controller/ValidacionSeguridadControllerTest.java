package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.ValidacionSeguridad;
import com.SaasRRHH.main.services.ValidacionSeguridadService;
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
class ValidacionSeguridadControllerTest {

    @Mock
    private ValidacionSeguridadService service;

    @InjectMocks
    private ValidacionSeguridadController controller;

    private ValidacionSeguridad validacion;

    @BeforeEach
    void setUp() {
        validacion = new ValidacionSeguridad();
        validacion.setId(1L);
    }

    @Test
    void listar_debeRetornarLista() {
        when(service.listar()).thenReturn(List.of(validacion));

        List<ValidacionSeguridad> resultado = controller.listar();

        assertEquals(1, resultado.size());
        verify(service).listar();
    }

    @Test
    void obtener_encontrado_debeRetornarOk() {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(validacion));

        ResponseEntity<ValidacionSeguridad> response = controller.obtener(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void obtener_noEncontrado_debeRetornar404() {
        when(service.buscarPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<ValidacionSeguridad> response = controller.obtener(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void crear_debeRetornarEntidad() {
        when(service.guardar(validacion)).thenReturn(validacion);

        ValidacionSeguridad resultado = controller.crear(validacion);

        assertEquals(1L, resultado.getId());
        verify(service).guardar(validacion);
    }

    @Test
    void actualizar_noEncontrado_debeRetornar404() {
        when(service.actualizar(22L, validacion)).thenReturn(Optional.empty());

        ResponseEntity<ValidacionSeguridad> response = controller.actualizar(22L, validacion);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void eliminar_debeInvocarServicio() {
        controller.eliminar(1L);

        verify(service).eliminar(1L);
    }
}
