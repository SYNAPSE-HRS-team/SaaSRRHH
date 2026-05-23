package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.DispositivoAutorizado;
import com.SaasRRHH.main.repository.DispositivoAutorizadoRepository;
import com.SaasRRHH.main.services.impl.DispositivoAutorizadoImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DispositivoAutorizadoImplTest {

    @Mock
    private DispositivoAutorizadoRepository repository;

    @InjectMocks
    private DispositivoAutorizadoImpl service;

    private DispositivoAutorizado dispositivo;

    @BeforeEach
    void setUp() {
        dispositivo = new DispositivoAutorizado();
        dispositivo.setId(1L);
    }

    @Test
    void listarTodo_debeRetornarLista() {
        when(repository.findAll()).thenReturn(List.of(dispositivo));

        List<DispositivoAutorizado> resultado = service.listarTodo();

        assertEquals(1, resultado.size());
        verify(repository).findAll();
    }

    @Test
    void buscarPorId_encontrado_debeRetornarOptional() {
        when(repository.findById(1L)).thenReturn(Optional.of(dispositivo));

        Optional<DispositivoAutorizado> resultado = service.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        verify(repository).findById(1L);
    }

    @Test
    void guardar_debePersistir() {
        when(repository.save(dispositivo)).thenReturn(dispositivo);

        DispositivoAutorizado resultado = service.guardar(dispositivo);

        assertEquals(1L, resultado.getId());
        verify(repository).save(dispositivo);
    }

    @Test
    void actualizar_debePersistirExistente() {
        when(repository.findById(1L)).thenReturn(Optional.of(dispositivo));
        when(repository.save(dispositivo)).thenReturn(dispositivo);

        DispositivoAutorizado resultado = service.actualizar(1L, dispositivo);

        assertEquals(1L, resultado.getId());
        verify(repository).findById(1L);
        verify(repository).save(dispositivo);
    }

    @Test
    void eliminar_debeInvocarDelete() {
        service.eliminar(1L);

        verify(repository).deleteById(1L);
    }
}
