package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.BoletaPagoRequestDTO;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.RegistroAsistencia;
import com.SaasRRHH.main.repository.EmpleadoRepository;
import com.SaasRRHH.main.repository.RegistroAsistenciaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NominaServiceImplTest {

    @Mock
    EmpleadoRepository empleadoRepository;

    @Mock
    RegistroAsistenciaRepository registroAsistenciaRepository;

    @InjectMocks
    NominaServiceImpl nominaService;

    @Test
    void calcularBoleta_basic() {
        Long empId = 1L;
        Empleado e = new Empleado();
        e.setId(empId);
        e.setSueldoBase(new BigDecimal("3000.00"));
        e.setAsignacionFamiliar(false);

        when(empleadoRepository.findById(empId)).thenReturn(Optional.of(e));

        RegistroAsistencia r1 = new RegistroAsistencia();
        r1.setTipoMarcacion("ENTRADA");
        r1.setEstado("VALIDADO");
        r1.setFechaHora(LocalDateTime.of(2026,5,3,8,0));

        RegistroAsistencia r2 = new RegistroAsistencia();
        r2.setTipoMarcacion("ENTRADA");
        r2.setEstado("VALIDADO");
        r2.setFechaHora(LocalDateTime.of(2026,5,4,8,0));

        RegistroAsistencia r3 = new RegistroAsistencia();
        r3.setTipoMarcacion("ENTRADA");
        r3.setEstado("VALIDADO");
        r3.setFechaHora(LocalDateTime.of(2026,5,5,8,0));

        when(registroAsistenciaRepository.findByEmpleadoIdAndFechaHoraBetween(eq(empId), any(), any()))
                .thenReturn(List.of(r1, r2, r3));

        BoletaPagoRequestDTO dto = nominaService.calcularBoleta(empId, 5, 2026);

        assertNotNull(dto);
        assertEquals(empId, dto.getEmpleadoId());
        assertEquals(new BigDecimal("3000.00"), dto.getSueldoBase());
        assertEquals(3, dto.getDiasTrabajados());
        assertNotNull(dto.getDescuentoInasistencia());
        assertTrue(dto.getNetoPagar().compareTo(BigDecimal.ZERO) > 0);
    }
}
