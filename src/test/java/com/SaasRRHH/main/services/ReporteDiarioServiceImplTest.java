package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.ReporteDiario;
import com.SaasRRHH.main.repository.ReporteDiarioRepository;
import com.SaasRRHH.main.services.impl.ReporteDiarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteDiarioServiceImplTest {

    @Mock
    private ReporteDiarioRepository repository;

    @InjectMocks
    private ReporteDiarioServiceImpl service;

    private ReporteDiario reporte;

    @BeforeEach
    void setUp() {
        reporte = new ReporteDiario();
        reporte.setId(1L);
    }

    @Test
    void listar_debeRetornarLista() {
        when(repository.findAll()).thenReturn(List.of(reporte));

        List<ReporteDiario> resultado = service.listar();

        assertEquals(1, resultado.size());
        verify(repository).findAll();
    }

    @Test
    void buscarPorId_encontrado_debeRetornarOptional() {
        when(repository.findById(1L)).thenReturn(Optional.of(reporte));

        Optional<ReporteDiario> resultado = service.buscarPorId(1L);

        assertEquals(true, resultado.isPresent());
    }

    @Test
    void guardar_debePersistir() {
        when(repository.save(reporte)).thenReturn(reporte);

        ReporteDiario resultado = service.guardar(reporte);

        assertEquals(1L, resultado.getId());
        verify(repository).save(reporte);
    }

    @Test
    void eliminar_debeInvocarDelete() {
        service.eliminar(1L);

        verify(repository).deleteById(1L);
    }
}
