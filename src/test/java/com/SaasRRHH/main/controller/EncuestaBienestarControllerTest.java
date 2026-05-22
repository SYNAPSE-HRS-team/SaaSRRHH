package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.Encuestabienestar;
import com.SaasRRHH.main.services.EncuestaBienestarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EncuestaBienestarControllerTest {

    @Mock
    private EncuestaBienestarService service;

    @InjectMocks
    private EncuestaBienestarController controller;

    private Encuestabienestar encuesta;

    @BeforeEach
    void setUp() {
        encuesta = new Encuestabienestar();
        encuesta.setId(1L);
    }

    @Test
    void listar_debeRetornarLista() {
        when(service.listar()).thenReturn(List.of(encuesta));

        List<Encuestabienestar> resultado = controller.listar();

        assertEquals(1, resultado.size());
        verify(service).listar();
    }

    @Test
    void obtener_debeRetornarEncuesta() {
        when(service.obtenerPorId(1L)).thenReturn(encuesta);

        Encuestabienestar resultado = controller.obtener(1L);

        assertEquals(1L, resultado.getId());
        verify(service).obtenerPorId(1L);
    }

    @Test
    void crear_debeRetornarEntidadCreada() {
        when(service.guardar(encuesta)).thenReturn(encuesta);

        Encuestabienestar resultado = controller.crear(encuesta);

        assertEquals(1L, resultado.getId());
        verify(service).guardar(encuesta);
    }

    @Test
    void actualizar_debeRetornarEntidadActualizada() {
        when(service.actualizar(1L, encuesta)).thenReturn(encuesta);

        Encuestabienestar resultado = controller.actualizar(1L, encuesta);

        assertEquals(1L, resultado.getId());
        verify(service).actualizar(1L, encuesta);
    }

    @Test
    void eliminar_debeInvocarServicio() {
        controller.eliminar(1L);

        verify(service).eliminar(1L);
    }
}
