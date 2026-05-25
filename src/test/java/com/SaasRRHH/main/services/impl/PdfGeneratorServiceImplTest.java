package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.model.BoletaPago;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.Planilla;
import com.SaasRRHH.main.repository.BoletaPagoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdfGeneratorServiceImplTest {

    @Mock
    BoletaPagoRepository boletaRepo;

    @InjectMocks
    PdfGeneratorServiceImpl pdfService;

    @Test
    void generarBoletaPdf_createsFile() throws Exception {
        Long id = 99L;
        BoletaPago b = new BoletaPago();
        b.setId(id);
        Empleado e = new Empleado(); e.setNombres("Juan"); e.setApellidos("Perez"); b.setEmpleado(e);
        Planilla p = new Planilla(); p.setMes(5); p.setAnio(2026); b.setPlanilla(p);
        b.setSueldoBase(new BigDecimal("1000.00"));
        b.setNetoPagar(new BigDecimal("800.00"));

        when(boletaRepo.findById(id)).thenReturn(Optional.of(b));
        when(boletaRepo.save(b)).thenReturn(b);

        byte[] pdf = pdfService.generarBoletaPdf(id);

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);

        Path file = Paths.get("storage", "boletas", "boleta-" + id + ".pdf");
        assertTrue(Files.exists(file));

        // cleanup
        Files.deleteIfExists(file);
    }
}
