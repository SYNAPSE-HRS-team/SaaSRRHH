package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.Familiar;
import com.SaasRRHH.main.repository.FamiliarRepository;
import com.SaasRRHH.main.services.impl.FamiliarServiceImpl;
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
class FamiliarServiceImplTest {

    @Mock
    private FamiliarRepository repository;

    @InjectMocks
    private FamiliarServiceImpl service;

    private Familiar familiar;

    @BeforeEach
    void setUp() {
        familiar = new Familiar();
        familiar.setId(1L);
    }

    @Test
    void listar_debeRetornarLista() {
        when(repository.findAll()).thenReturn(List.of(familiar));

        List<Familiar> resultado = service.listar();

        assertEquals(1, resultado.size());
        verify(repository).findAll();
    }

    @Test
    void buscarPorId_encontrado_debeRetornarOptional() {
        when(repository.findById(1L)).thenReturn(Optional.of(familiar));

        Optional<Familiar> resultado = service.buscarPorId(1L);

        assertEquals(true, resultado.isPresent());
        verify(repository).findById(1L);
    }

    @Test
    void guardar_debePersistir() {
        when(repository.save(familiar)).thenReturn(familiar);

        Familiar resultado = service.guardar(familiar);

        assertEquals(1L, resultado.getId());
        verify(repository).save(familiar);
    }

    @Test
    void actualizar_debePersistirExistente() {
        when(repository.findById(1L)).thenReturn(Optional.of(familiar));
        when(repository.save(familiar)).thenReturn(familiar);

        Familiar actualizado = new Familiar();
        actualizado.setNombres("Ana");

        Familiar resultado = service.actualizar(1L, actualizado);

        assertEquals(1L, resultado.getId());
        verify(repository).findById(1L);
        verify(repository).save(familiar);
    }

    @Test
    void eliminar_debeInvocarDelete() {
        service.eliminar(1L);

        verify(repository).deleteById(1L);
    }
}
