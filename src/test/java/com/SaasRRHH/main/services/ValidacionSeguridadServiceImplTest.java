package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.ValidacionSeguridad;
import com.SaasRRHH.main.repository.ValidacionSeguridadRepository;
import com.SaasRRHH.main.services.impl.ValidacionSeguridadServiceImpl;
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
class ValidacionSeguridadServiceImplTest {

    @Mock
    private ValidacionSeguridadRepository repository;

    @InjectMocks
    private ValidacionSeguridadServiceImpl service;

    private ValidacionSeguridad validacion;

    @BeforeEach
    void setUp() {
        validacion = new ValidacionSeguridad();
        validacion.setId(1L);
    }

    @Test
    void listar_debeRetornarLista() {
        when(repository.findAll()).thenReturn(List.of(validacion));

        List<ValidacionSeguridad> resultado = service.listar();

        assertEquals(1, resultado.size());
        verify(repository).findAll();
    }

    @Test
    void buscarPorId_encontrado_debeRetornarOptional() {
        when(repository.findById(1L)).thenReturn(Optional.of(validacion));

        Optional<ValidacionSeguridad> resultado = service.buscarPorId(1L);

        assertEquals(true, resultado.isPresent());
    }

    @Test
    void guardar_debePersistir() {
        when(repository.save(validacion)).thenReturn(validacion);

        ValidacionSeguridad resultado = service.guardar(validacion);

        assertEquals(1L, resultado.getId());
    }

    @Test
    void eliminar_debeInvocarDelete() {
        service.eliminar(1L);

        verify(repository).deleteById(1L);
    }
}
