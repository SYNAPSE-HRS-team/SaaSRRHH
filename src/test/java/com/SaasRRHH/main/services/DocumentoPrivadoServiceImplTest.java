package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.DocumentoPrivado;
import com.SaasRRHH.main.repository.DocumentoPrivadoRepository;
import com.SaasRRHH.main.services.impl.DocumentoPrivadoServiceImpl;
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
class DocumentoPrivadoServiceImplTest {

    @Mock
    private DocumentoPrivadoRepository repository;

    @InjectMocks
    private DocumentoPrivadoServiceImpl service;

    private DocumentoPrivado documento;

    @BeforeEach
    void setUp() {
        documento = new DocumentoPrivado();
        documento.setId(1L);
    }

    @Test
    void listar_debeRetornarLista() {
        when(repository.findAll()).thenReturn(List.of(documento));

        List<DocumentoPrivado> resultado = service.listar();

        assertEquals(1, resultado.size());
        verify(repository).findAll();
    }

    @Test
    void buscarPorId_encontrado_debeRetornarOptional() {
        when(repository.findById(1L)).thenReturn(Optional.of(documento));

        Optional<DocumentoPrivado> resultado = service.buscarPorId(1L);

        assertEquals(true, resultado.isPresent());
        verify(repository).findById(1L);
    }

    @Test
    void guardar_debePersistir() {
        when(repository.save(documento)).thenReturn(documento);

        DocumentoPrivado resultado = service.guardar(documento);

        assertEquals(1L, resultado.getId());
        verify(repository).save(documento);
    }

    @Test
    void actualizar_debePersistirExistente() {
        when(repository.findById(1L)).thenReturn(Optional.of(documento));
        when(repository.save(documento)).thenReturn(documento);

        DocumentoPrivado resultado = service.actualizar(1L, documento);

        assertEquals(1L, resultado.getId());
        verify(repository).findById(1L);
        verify(repository).save(documento);
    }
}
