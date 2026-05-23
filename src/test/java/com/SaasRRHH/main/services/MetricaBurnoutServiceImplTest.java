package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.MetricaBurnout;
import com.SaasRRHH.main.repository.MetricaBurnoutRepository;
import com.SaasRRHH.main.services.impl.MetricaBurnoutServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetricaBurnoutServiceImplTest {

    @Mock
    private MetricaBurnoutRepository repository;

    @InjectMocks
    private MetricaBurnoutServiceImpl service;

    private MetricaBurnout metrica;

    @BeforeEach
    void setUp() {
        metrica = new MetricaBurnout();
        metrica.setId(1L);
    }

    @Test
    void listar_debeRetornarLista() {
        when(repository.findAll()).thenReturn(List.of(metrica));

        List<MetricaBurnout> resultado = service.listar();

        assertEquals(1, resultado.size());
        verify(repository).findAll();
    }

    @Test
    void obtenerPorId_encontrado_debeRetornarEntidad() {
        when(repository.findById(1L)).thenReturn(Optional.of(metrica));

        MetricaBurnout resultado = service.obtenerPorId(1L);

        assertEquals(1L, resultado.getId());
    }

    @Test
    void obtenerPorId_noEncontrado_debeLanzarExcepcion() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.obtenerPorId(9L));
    }

    @Test
    void guardar_debePersistir() {
        when(repository.save(metrica)).thenReturn(metrica);

        MetricaBurnout resultado = service.guardar(metrica);

        assertEquals(1L, resultado.getId());
        verify(repository).save(metrica);
    }

    @Test
    void buscarPorEmpleado_debeRetornarLista() {
        when(repository.findByEmpleadoId(1L)).thenReturn(List.of(metrica));

        List<MetricaBurnout> resultado = service.buscarPorEmpleado(1L);

        assertEquals(1, resultado.size());
        verify(repository).findByEmpleadoId(1L);
    }

    @Test
    void actualizar_debeGuardarCambios() {
        MetricaBurnout metricaActualizada = new MetricaBurnout();
        metricaActualizada.setNivelRiesgo(MetricaBurnout.NivelRiesgoBurnout.ALTO);

        when(repository.findById(1L)).thenReturn(Optional.of(metrica));
        when(repository.save(metrica)).thenReturn(metrica);

        MetricaBurnout resultado = service.actualizar(1L, metricaActualizada);

        assertEquals(1L, resultado.getId());
        verify(repository).save(metrica);
    }

    @Test
    void eliminar_debeInvocarDelete() {
        service.eliminar(1L);

        verify(repository).deleteById(1L);
    }
}
