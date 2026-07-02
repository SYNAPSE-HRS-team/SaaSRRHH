package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.EmpleadoResponseDTO;
import com.SaasRRHH.main.DTO.RegistroAsistenciaResponseDTO;
import com.SaasRRHH.main.model.RegistroAsistencia;
import com.SaasRRHH.main.repository.RegistroAsistenciaRepository;
import com.SaasRRHH.main.services.EmpleadoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistroAsistenciaServiceImplTest {

        @Mock
        RegistroAsistenciaRepository repository;

        @Mock
        EmpleadoService empleadoService;

        @InjectMocks
        RegistroAsistenciaServiceImpl service;

        @Test
        void registrarEntrada_guardaAsistenciaCuandoNoExisteRegistroPrevio() {
                Long empleadoId = 10L;

                when(empleadoService.buscarPorId(empleadoId))
                                .thenReturn(new EmpleadoResponseDTO());
                when(repository.yaMarcoHoy(eq(empleadoId), any(), any(), eq("ENTRADA")))
                                .thenReturn(false);
                when(repository.save(any(RegistroAsistencia.class)))
                                .thenAnswer(invocation -> {
                                        RegistroAsistencia registro = invocation.getArgument(0);
                                        registro.setId(99L);
                                        return registro;
                                });

                RegistroAsistenciaResponseDTO response = service.registrarEntrada(empleadoId, null);

                assertEquals(99L, response.getId());
                assertEquals(empleadoId, response.getEmpleadoId());
                assertEquals("ENTRADA", response.getTipoMarcacion());
                assertEquals("QR", response.getMetodo());
                assertEquals("VALIDADO", response.getEstado());
                assertTrue(response.getFechaHora() != null);
                verify(repository).save(any(RegistroAsistencia.class));
        }

        @Test
        void registrarEntrada_lanzaErrorCuandoYaMarcoHoy() {
                Long empleadoId = 10L;

                when(empleadoService.buscarPorId(empleadoId))
                                .thenReturn(new EmpleadoResponseDTO());
                when(repository.yaMarcoHoy(eq(empleadoId), any(), any(), eq("ENTRADA")))
                                .thenReturn(true);

                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> service.registrarEntrada(empleadoId, "QR"));

                assertEquals("El empleado ya registró entrada hoy", exception.getMessage());
                verify(repository, never()).save(any(RegistroAsistencia.class));
        }

        @Test
        void asistenciasHoy_usaElRangoCompletoDelDia() {
                RegistroAsistencia registro = new RegistroAsistencia();
                registro.setId(1L);
                registro.setFechaHora(LocalDateTime.of(2026, 5, 25, 10, 0));

                when(repository.asistenciasHoy(any(), any()))
                                .thenReturn(List.of(registro));

                List<RegistroAsistenciaResponseDTO> response = service.asistenciasHoy();

                assertEquals(1, response.size());
                assertEquals(1L, response.get(0).getId());

                ArgumentCaptor<LocalDateTime> inicioCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
                ArgumentCaptor<LocalDateTime> finCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

                verify(repository).asistenciasHoy(inicioCaptor.capture(), finCaptor.capture());

                assertEquals(0, inicioCaptor.getValue().toLocalTime().getHour());
                assertEquals(0, inicioCaptor.getValue().toLocalTime().getMinute());
                assertEquals(0, inicioCaptor.getValue().toLocalTime().getSecond());
                assertEquals(0, inicioCaptor.getValue().toLocalTime().getNano());

                assertEquals(0, finCaptor.getValue().toLocalTime().getHour());
                assertEquals(0, finCaptor.getValue().toLocalTime().getMinute());
                assertEquals(0, finCaptor.getValue().toLocalTime().getSecond());
                assertEquals(0, finCaptor.getValue().toLocalTime().getNano());
                assertEquals(inicioCaptor.getValue().plusDays(1), finCaptor.getValue());
        }
}