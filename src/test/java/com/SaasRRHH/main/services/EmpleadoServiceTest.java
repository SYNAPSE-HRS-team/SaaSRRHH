package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.repository.EmpleadoRepository;
import com.SaasRRHH.main.services.impl.EmpleadoServiceImpl;
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
class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository repository;

    @InjectMocks
    private EmpleadoServiceImpl service;

    private Empleado empleado;

    @BeforeEach
    void setUp() {
        empleado = new Empleado();
        empleado.setId(1L);
    }

    @Test
    void testListar() {
        when(repository.findAll()).thenReturn(List.of(empleado));

        List<Empleado> resultado = service.listar();

        assertEquals(1, resultado.size());
        verify(repository).findAll();
    }


    @Test
    void testBuscarPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(empleado));

        Optional<Empleado> resultado = service.buscarPorId(1L);

        assertEquals(true, resultado.isPresent());
        verify(repository).findById(1L);
    }

    @Test
    void testGuardar() {
        when(repository.save(empleado)).thenReturn(empleado);

        Empleado resultado = service.guardar(empleado);

        assertEquals(1L, resultado.getId());
        verify(repository).save(empleado);
    }

    @Test
    void testEliminar() {
        service.eliminar(1L);

        verify(repository).deleteById(1L);
    }
}
