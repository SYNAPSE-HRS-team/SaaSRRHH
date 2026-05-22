package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.AccesoUsuario;
import com.SaasRRHH.main.repository.AccesoUsuarioRepository;
import com.SaasRRHH.main.services.impl.AccesoUsuarioServiceImpl;
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
class AccesoUsuarioServiceImplTest {

    @Mock
    private AccesoUsuarioRepository repository;

    @InjectMocks
    private AccesoUsuarioServiceImpl service;

    private AccesoUsuario acceso;

    @BeforeEach
    void setUp() {
        acceso = new AccesoUsuario();
    }

    @Test
    void listar_debeRetornarLista() {
        when(repository.findAll()).thenReturn(List.of(acceso));

        List<AccesoUsuario> resultado = service.listar();

        assertEquals(1, resultado.size());
        verify(repository).findAll();
    }

    @Test
    void buscarPorId_encontrado_debeRetornarEntidad() {
        when(repository.findById(1L)).thenReturn(Optional.of(acceso));

        AccesoUsuario resultado = service.buscarPorId(1L);

        assertEquals(acceso, resultado);
        verify(repository).findById(1L);
    }

    @Test
    void buscarPorId_noEncontrado_debeLanzarExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.buscarPorId(99L));
        verify(repository).findById(99L);
    }

    @Test
    void guardar_debePersistir() {
        when(repository.save(acceso)).thenReturn(acceso);

        AccesoUsuario resultado = service.guardar(acceso);

        assertEquals(acceso, resultado);
        verify(repository).save(acceso);
    }
}
