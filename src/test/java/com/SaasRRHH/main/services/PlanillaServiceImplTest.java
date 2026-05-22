package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.Planilla;
import com.SaasRRHH.main.repository.PlanillaRepository;
import com.SaasRRHH.main.services.impl.PlanillaServiceImpl;
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
class PlanillaServiceImplTest {

    @Mock
    private PlanillaRepository repository;

    @InjectMocks
    private PlanillaServiceImpl service;

    private Planilla planilla;

    @BeforeEach
    void setUp() {
        planilla = new Planilla();
        planilla.setId(1L);
    }

    @Test
    void listar_debeRetornarLista() {
        when(repository.findAll()).thenReturn(List.of(planilla));

        List<Planilla> resultado = service.listar();

        assertEquals(1, resultado.size());
        verify(repository).findAll();
    }

    @Test
    void buscarPorId_encontrado_debeRetornarOptional() {
        when(repository.findById(1L)).thenReturn(Optional.of(planilla));

        Optional<Planilla> resultado = service.buscarPorId(1L);

        assertEquals(true, resultado.isPresent());
    }

    @Test
    void guardar_debePersistir() {
        when(repository.save(planilla)).thenReturn(planilla);

        Planilla resultado = service.guardar(planilla);

        assertEquals(1L, resultado.getId());
        verify(repository).save(planilla);
    }

    @Test
    void eliminar_debeInvocarDelete() {
        service.eliminar(1L);

        verify(repository).deleteById(1L);
    }
}
