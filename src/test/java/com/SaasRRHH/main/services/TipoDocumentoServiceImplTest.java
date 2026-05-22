package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.TipoDocumento;
import com.SaasRRHH.main.repository.TipoDocumentoRepository;
import com.SaasRRHH.main.services.impl.TipoDocumentoServiceImpl;
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
class TipoDocumentoServiceImplTest {

    @Mock
    private TipoDocumentoRepository repository;

    @InjectMocks
    private TipoDocumentoServiceImpl service;

    private TipoDocumento tipoDocumento;

    @BeforeEach
    void setUp() {
        tipoDocumento = new TipoDocumento();
        tipoDocumento.setIdTipo(1L);
    }

    @Test
    void listar_debeRetornarLista() {
        when(repository.findAll()).thenReturn(List.of(tipoDocumento));

        List<TipoDocumento> resultado = service.listar();

        assertEquals(1, resultado.size());
        verify(repository).findAll();
    }

    @Test
    void buscarPorId_encontrado_debeRetornarOptional() {
        when(repository.findById(1L)).thenReturn(Optional.of(tipoDocumento));

        Optional<TipoDocumento> resultado = service.buscarPorId(1L);

        assertEquals(true, resultado.isPresent());
    }

    @Test
    void guardar_debePersistir() {
        when(repository.save(tipoDocumento)).thenReturn(tipoDocumento);

        TipoDocumento resultado = service.guardar(tipoDocumento);

        assertEquals(1L, resultado.getIdTipo());
    }

    @Test
    void eliminar_debeInvocarDelete() {
        service.eliminar(1L);

        verify(repository).deleteById(1L);
    }
}
