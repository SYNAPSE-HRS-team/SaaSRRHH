package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.AreaTrabajo;
import com.SaasRRHH.main.repository.AreaTrabajoRepository;
import com.SaasRRHH.main.services.impl.AreaTrabajoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AreaTrabajoServiceTest {

    @Mock
    private AreaTrabajoRepository areaRepository;

    @InjectMocks
    private AreaTrabajoServiceImpl areaService;

    private AreaTrabajo area1;
    private AreaTrabajo area2;

    @BeforeEach
    void setUp() {
        area1 = new AreaTrabajo();
        area1.setId(1L);
        area1.setNombre("Parcela Norte");
        area1.setCultivoTipo("Papa");
        area1.setActivo(true);

        area2 = new AreaTrabajo();
        area2.setId(2L);
        area2.setNombre("Parcela Sur");
        area2.setCultivoTipo("Maíz");
        area2.setActivo(true);
    }

    @Test
    void testListarAreas_Exitoso() {
        when(areaRepository.findAll()).thenReturn(Arrays.asList(area1, area2));

        List<AreaTrabajo> resultado = areaService.listar();

        assertThat(resultado).hasSize(2);
        verify(areaRepository, times(1)).findAll();
    }

    @Test
    void testBuscarPorId_Encontrado() {
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area1));

        Optional<AreaTrabajo> resultado = areaService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Parcela Norte");
        verify(areaRepository, times(1)).findById(1L);
    }

    @Test
    void testGuardarArea_NuevaExitoso() {
        AreaTrabajo nuevaArea = new AreaTrabajo();
        nuevaArea.setNombre("Parcela Este");
        nuevaArea.setCultivoTipo("Tomate");

        when(areaRepository.existsByNombre("Parcela Este")).thenReturn(false);
        when(areaRepository.save(any(AreaTrabajo.class))).thenReturn(nuevaArea);

        AreaTrabajo resultado = areaService.guardar(nuevaArea);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Parcela Este");
        verify(areaRepository, times(1)).save(nuevaArea);
    }

    @Test
    void testGuardarArea_NombreDuplicado_LanzaExcepcion() {
        AreaTrabajo areaDuplicada = new AreaTrabajo();
        areaDuplicada.setNombre("Parcela Norte");

        when(areaRepository.existsByNombre("Parcela Norte")).thenReturn(true);

        assertThatThrownBy(() -> areaService.guardar(areaDuplicada))
                .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Ya existe un area");
        
        verify(areaRepository, never()).save(any());
    }
}