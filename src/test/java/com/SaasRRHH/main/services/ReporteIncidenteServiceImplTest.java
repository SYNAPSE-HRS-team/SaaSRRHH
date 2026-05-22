package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.ReporteIncidente;
import com.SaasRRHH.main.repository.ReporteIncidenteRepository;
import com.SaasRRHH.main.services.impl.ReporteIncidenteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteIncidenteServiceImplTest {

    @Mock
    private ReporteIncidenteRepository repository;

    @InjectMocks
    private ReporteIncidenteServiceImpl service;

    private ReporteIncidente reporte;

    @BeforeEach
    void setUp() {
        reporte = new ReporteIncidente();
        reporte.setId(1L);
    }

    @Test
    void listar_debeRetornarLista() {
        when(repository.findAll()).thenReturn(List.of(reporte));

        List<ReporteIncidente> resultado = service.listar();

        assertEquals(1, resultado.size());
        verify(repository).findAll();
    }

    @Test
    void obtenerPorId_noEncontrado_debeRetornarNull() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        ReporteIncidente resultado = service.obtenerPorId(9L);

        assertNull(resultado);
    }

    @Test
    void guardar_debePersistir() {
        when(repository.save(reporte)).thenReturn(reporte);

        ReporteIncidente resultado = service.guardar(reporte);

        assertEquals(1L, resultado.getId());
    }

    @Test
    void eliminar_debeInvocarDelete() {
        service.eliminar(1L);

        verify(repository).deleteById(1L);
    }
}
