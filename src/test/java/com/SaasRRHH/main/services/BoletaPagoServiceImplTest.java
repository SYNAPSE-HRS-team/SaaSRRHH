package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.BoletaPago;
import com.SaasRRHH.main.repository.BoletaPagoRepository;
import com.SaasRRHH.main.services.impl.BoletaPagoServiceImpl;
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
class BoletaPagoServiceImplTest {

    @Mock
    private BoletaPagoRepository repository;

    @InjectMocks
    private BoletaPagoServiceImpl service;

    private BoletaPago boleta;

    @BeforeEach
    void setUp() {
        boleta = new BoletaPago();
        boleta.setId(1L);
    }

    @Test
    void listar_debeRetornarLista() {
        when(repository.findAll()).thenReturn(List.of(boleta));

        List<BoletaPago> resultado = service.listar();

        assertEquals(1, resultado.size());
        verify(repository).findAll();
    }

    @Test
    void buscarPorId_encontrado_debeRetornarOptional() {
        when(repository.findById(1L)).thenReturn(Optional.of(boleta));

        Optional<BoletaPago> resultado = service.buscarPorId(1L);

        assertEquals(true, resultado.isPresent());
        verify(repository).findById(1L);
    }

    @Test
    void actualizar_debeGuardarConId() {
        when(repository.save(boleta)).thenReturn(boleta);

        BoletaPago resultado = service.actualizar(1L, boleta);

        assertEquals(1L, resultado.getId());
        verify(repository).save(boleta);
    }

    @Test
    void eliminar_debeInvocarDelete() {
        service.eliminar(1L);

        verify(repository).deleteById(1L);
    }
}
