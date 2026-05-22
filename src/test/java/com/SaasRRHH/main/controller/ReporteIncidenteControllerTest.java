package com.SaasRRHH.main.controller;

import com.SaasRRHH.main.model.ReporteIncidente;
import com.SaasRRHH.main.services.ReporteIncidenteService;
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
class ReporteIncidenteControllerTest {

    @Mock
    private ReporteIncidenteService service;

    @InjectMocks
    private ReporteIncidenteController controller;

    private ReporteIncidente reporte;

    @BeforeEach
    void setUp() {
        reporte = new ReporteIncidente();
        reporte.setId(1L);
    }

    @Test
    void listar_debeRetornarLista() {
        when(service.listar()).thenReturn(List.of(reporte));

        List<ReporteIncidente> resultado = controller.listar();

        assertEquals(1, resultado.size());
        verify(service).listar();
    }

    @Test
    void obtener_debeRetornarEntidad() {
        when(service.obtenerPorId(1L)).thenReturn(reporte);

        ReporteIncidente resultado = controller.obtener(1L);

        assertEquals(1L, resultado.getId());
        verify(service).obtenerPorId(1L);
    }

    @Test
    void crear_debeRetornarEntidadCreada() {
        when(service.guardar(reporte)).thenReturn(reporte);

        ReporteIncidente resultado = controller.crear(reporte);

        assertEquals(1L, resultado.getId());
        verify(service).guardar(reporte);
    }

    @Test
    void actualizar_debeRetornarEntidadActualizada() {
        when(service.actualizar(1L, reporte)).thenReturn(reporte);

        ReporteIncidente resultado = controller.actualizar(1L, reporte);

        assertEquals(1L, resultado.getId());
        verify(service).actualizar(1L, reporte);
    }

    @Test
    void eliminar_debeInvocarServicio() {
        controller.eliminar(1L);

        verify(service).eliminar(1L);
    }
}
