package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.DashboardDTO;
import com.SaasRRHH.main.repository.EmpleadoRepository;
import com.SaasRRHH.main.repository.RegistroAsistenciaRepository;
import com.SaasRRHH.main.repository.ReporteDiarioRepository;
import com.SaasRRHH.main.repository.ReporteIncidenteRepository;
import com.SaasRRHH.main.repository.UsuarioRepository;
import com.SaasRRHH.main.services.AnaliticaService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnaliticaServiceImpl implements AnaliticaService {

    private final EmpleadoRepository empleadoRepository;

    private final UsuarioRepository usuarioRepository;

    private final ReporteDiarioRepository reporteDiarioRepository;

    private final ReporteIncidenteRepository reporteIncidenteRepository;

    private final RegistroAsistenciaRepository registroAsistenciaRepository;

    @Override
    public DashboardDTO obtenerDashboard() {

        long totalEmpleados =
                empleadoRepository.count();

        long totalUsuarios =
                usuarioRepository.count();

        long reportesDiarios =
                reporteDiarioRepository.count();

        long incidentes =
                reporteIncidenteRepository.count();

        // AUSENCIAS REALES DESDE POSTGRESQL
        long ausencias =
                registroAsistenciaRepository.countByEstado("RECHAZADO");

        // PORCENTAJE AUSENTISMO
        double porcentajeAusentismo = 0;

        if (totalEmpleados > 0) {

            porcentajeAusentismo =
                    (ausencias * 100.0) / totalEmpleados;
        }

        // NIVEL DE RIESGO
        String nivelRiesgo;

        if (incidentes >= 6) {

            nivelRiesgo = "ALTO";

        } else if (incidentes >= 3) {

            nivelRiesgo = "MEDIO";

        } else {

            nivelRiesgo = "BAJO";
        }

        return new DashboardDTO(
                totalEmpleados,
                totalUsuarios,
                reportesDiarios,
                ausencias,
                incidentes,
                porcentajeAusentismo,
                nivelRiesgo
        );
    }
}