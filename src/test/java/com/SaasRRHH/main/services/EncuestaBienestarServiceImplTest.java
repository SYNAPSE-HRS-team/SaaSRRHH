package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.Encuestabienestar;
import com.SaasRRHH.main.repository.EncuestaBienestarRepository;
import com.SaasRRHH.main.services.impl.EncuestaBienestarServiceImpl;
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
class EncuestaBienestarServiceImplTest {

    @Mock
    private EncuestaBienestarRepository repository;

    @InjectMocks
    private EncuestaBienestarServiceImpl service;

    private Encuestabienestar encuesta;

    @BeforeEach
    void setUp() {
        encuesta = new Encuestabienestar();
        encuesta.setId(1L);
    }

    @Test
    void listar_debeRetornarLista() {
        when(repository.findAll()).thenReturn(List.of(encuesta));

        List<Encuestabienestar> resultado = service.listar();

        assertEquals(1, resultado.size());
        verify(repository).findAll();
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeRetornarNull() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        Encuestabienestar resultado = service.obtenerPorId(9L);

        assertNull(resultado);
        verify(repository).findById(9L);
    }

    @Test
    void guardar_debePersistir() {
        when(repository.save(encuesta)).thenReturn(encuesta);

        Encuestabienestar resultado = service.guardar(encuesta);

        assertEquals(1L, resultado.getId());
        verify(repository).save(encuesta);
    }

    @Test
    void eliminar_debeInvocarDelete() {
        service.eliminar(1L);

        verify(repository).deleteById(1L);
    }
}
