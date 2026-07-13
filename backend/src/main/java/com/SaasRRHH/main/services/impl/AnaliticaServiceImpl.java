package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.DashboardDTO;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.FeedbackAnonimo;
import com.SaasRRHH.main.model.MetricaBurnout;
import com.SaasRRHH.main.model.RegistroAsistencia;
import com.SaasRRHH.main.repository.EmpleadoRepository;
import com.SaasRRHH.main.repository.FeedbackAnonimoRepository;
import com.SaasRRHH.main.repository.MetricaBurnoutRepository;
import com.SaasRRHH.main.repository.RegistroAsistenciaRepository;
import com.SaasRRHH.main.repository.ReporteDiarioRepository;
import com.SaasRRHH.main.repository.ReporteIncidenteRepository;
import com.SaasRRHH.main.repository.UsuarioRepository;
import com.SaasRRHH.main.services.AnaliticaService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnaliticaServiceImpl implements AnaliticaService {

    private static final Logger log = LoggerFactory.getLogger(AnaliticaServiceImpl.class);

    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReporteDiarioRepository reporteDiarioRepository;
    private final ReporteIncidenteRepository reporteIncidenteRepository;
    private final RegistroAsistenciaRepository registroAsistenciaRepository;
    private final MetricaBurnoutRepository metricaBurnoutRepository;
    private final FeedbackAnonimoRepository feedbackAnonimoRepository;

    @Override
    public DashboardDTO obtenerDashboard() {

        log.info("📊 Generando dashboard general");

        // ============================================
        // MÉTRICAS BÁSICAS (ORIGINALES)
        // ============================================
        long totalEmpleados = empleadoRepository.count();
        long totalUsuarios = usuarioRepository.count();
        long reportesDiarios = reporteDiarioRepository.count();
        long incidentes = reporteIncidenteRepository.count();
        long ausencias = registroAsistenciaRepository.countByEstado("RECHAZADO");

        // Porcentaje ausentismo
        double porcentajeAusentismo = 0;
        if (totalEmpleados > 0) {
            porcentajeAusentismo = (ausencias * 100.0) / totalEmpleados;
        }

        // Nivel de riesgo organizacional
        String nivelRiesgo;
        if (incidentes >= 6) {
            nivelRiesgo = "ALTO";
        } else if (incidentes >= 3) {
            nivelRiesgo = "MEDIO";
        } else {
            nivelRiesgo = "BAJO";
        }

        // ============================================
        // ✅ NUEVAS MÉTRICAS AVANZADAS
        // ============================================

        // 1. Empleados con riesgo ALTO
        long empleadosRiesgoAlto = metricaBurnoutRepository.findAllWithRelaciones()
                .stream()
                .filter(m -> m.getNivelRiesgo() == MetricaBurnout.NivelRiesgoBurnout.ALTO)
                .map(m -> m.getEmpleado().getId())
                .distinct()
                .count();
        log.info("   Empleados con riesgo ALTO: {}", empleadosRiesgoAlto);

        // 2. Total alertas activas (empleados con riesgo ALTO + incidentes sin resolver)
        long totalAlertas = empleadosRiesgoAlto + incidentes;
        log.info("   Total alertas activas: {}", totalAlertas);

        // 3. Promedio de puntualidad general
        double promedioPuntualidad = calcularPromedioPuntualidad();
        log.info("   Promedio de puntualidad: {}%", promedioPuntualidad);

        // 4. Faltas del día actual
        long totalFaltasHoy = contarFaltasHoy();
        log.info("   Faltas hoy: {}", totalFaltasHoy);

        // 5. Tardanzas del día actual
        long totalTardanzasHoy = contarTardanzasHoy();
        log.info("   Tardanzas hoy: {}", totalTardanzasHoy);

        // 6. Feedback pendiente
        long feedbackPendientes = feedbackAnonimoRepository.countByEstado(FeedbackAnonimo.EstadoFeedback.PENDIENTE);
        log.info("   Feedback pendientes: {}", feedbackPendientes);

        // 7. ✅ Ranking de bajo desempeño
        List<DashboardDTO.EmpleadoAlertaDTO> rankingBajoDesempeno = generarRankingBajoDesempeno();
        log.info("   Empleados en ranking de bajo desempeño: {}", rankingBajoDesempeno.size());

        // ============================================
        // CONSTRUIR DTO
        // ============================================
        DashboardDTO dashboard = new DashboardDTO();
        dashboard.setTotalEmpleados(totalEmpleados);
        dashboard.setTotalUsuarios(totalUsuarios);
        dashboard.setReportesDiarios(reportesDiarios);
        dashboard.setAusencias(ausencias);
        dashboard.setIncidentes(incidentes);
        dashboard.setPorcentajeAusentismo(porcentajeAusentismo);
        dashboard.setNivelRiesgo(nivelRiesgo);
        
        // ✅ Nuevos campos
        dashboard.setEmpleadosRiesgoAlto(empleadosRiesgoAlto);
        dashboard.setTotalAlertas(totalAlertas);
        dashboard.setPromedioPuntualidad(promedioPuntualidad);
        dashboard.setTotalFaltasHoy(totalFaltasHoy);
        dashboard.setTotalTardanzasHoy(totalTardanzasHoy);
        dashboard.setFeedbackPendientes(feedbackPendientes);
        dashboard.setRankingBajoDesempeno(rankingBajoDesempeno);

        return dashboard;
    }

    // ============================================
    // ✅ NUEVOS MÉTODOS DE CÁLCULO
    // ============================================

    /**
     * Calcula el promedio de puntualidad de todos los empleados
     */
    private double calcularPromedioPuntualidad() {
        List<MetricaBurnout> ultimasMetricas = metricaBurnoutRepository.findAllWithRelaciones();
        
        if (ultimasMetricas.isEmpty()) return 100.0;
        
        // Agrupar por empleado y tomar la última métrica
        var ultimasPorEmpleado = ultimasMetricas.stream()
                .collect(Collectors.groupingBy(
                    m -> m.getEmpleado().getId(),
                    Collectors.maxBy((a, b) -> a.getFechaEvaluacion().compareTo(b.getFechaEvaluacion()))
                ));
        
        double suma = ultimasPorEmpleado.values().stream()
                .filter(opt -> opt.isPresent())
                .mapToDouble(opt -> {
                    Double indice = opt.get().getIndicePuntualidad();
                    return indice != null ? indice : 100.0;
                })
                .sum();
        
        return ultimasPorEmpleado.isEmpty() ? 100.0 : suma / ultimasPorEmpleado.size();
    }

    /**
     * Cuenta las faltas del día actual
     */
    private long contarFaltasHoy() {
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.plusDays(1).atStartOfDay();
        
        // Empleados activos que deberían trabajar hoy
        List<Empleado> empleadosActivos = empleadoRepository.findByActivoTrue();
        
        long faltas = 0;
        for (Empleado emp : empleadosActivos) {
            // Verificar si hoy es día laborable para este empleado
            if (emp.getDiasLaborables() == null || emp.esDiaLaborable(hoy.getDayOfWeek())) {
                // Verificar si marcó entrada hoy
                List<RegistroAsistencia> registrosHoy = registroAsistenciaRepository
                        .findByEmpleadoIdAndFechaHoraBetween(emp.getId(), inicio, fin);
                
                boolean marcoEntrada = registrosHoy.stream()
                        .anyMatch(r -> "ENTRADA".equals(r.getTipoMarcacion()) 
                                && !"RECHAZADO".equals(r.getEstado()));
                
                if (!marcoEntrada) {
                    faltas++;
                }
            }
        }
        
        return faltas;
    }

    /**
     * Cuenta las tardanzas del día actual
     */
    private long contarTardanzasHoy() {
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.plusDays(1).atStartOfDay();
        
        List<RegistroAsistencia> registrosHoy = registroAsistenciaRepository
                .asistenciasHoy(inicio, fin);
        
        return registrosHoy.stream()
                .filter(r -> "ENTRADA".equals(r.getTipoMarcacion()))
                .filter(r -> r.getMinutosTardanza() != null && r.getMinutosTardanza() > 0)
                .count();
    }

    /**
     * ✅ Genera el ranking de empleados con bajo desempeño
     * Basado en: nivel de riesgo, índice de puntualidad, faltas y tardanzas
     */
    private List<DashboardDTO.EmpleadoAlertaDTO> generarRankingBajoDesempeno() {
        List<MetricaBurnout> todasMetricas = metricaBurnoutRepository.findAllWithRelaciones();
        
        if (todasMetricas.isEmpty()) return new ArrayList<>();
        
        // Agrupar por empleado y tomar la última métrica de cada uno
        var ultimasPorEmpleado = todasMetricas.stream()
                .collect(Collectors.groupingBy(
                    m -> m.getEmpleado().getId(),
                    Collectors.maxBy((a, b) -> a.getFechaEvaluacion().compareTo(b.getFechaEvaluacion()))
                ));
        
        List<DashboardDTO.EmpleadoAlertaDTO> ranking = new ArrayList<>();
        
        for (var entry : ultimasPorEmpleado.entrySet()) {
            if (entry.getValue().isPresent()) {
                MetricaBurnout metrica = entry.getValue().get();
                Empleado empleado = metrica.getEmpleado();
                
                // Solo incluir empleados con riesgo MEDIO o ALTO
                if (metrica.getNivelRiesgo() == MetricaBurnout.NivelRiesgoBurnout.BAJO) {
                    continue;
                }
                
                DashboardDTO.EmpleadoAlertaDTO alerta = new DashboardDTO.EmpleadoAlertaDTO();
                alerta.setEmpleadoId(empleado.getId());
                alerta.setNombreEmpleado(empleado.getNombres() + " " + empleado.getApellidos());
                alerta.setCargo(empleado.getCargo());
                alerta.setNivelRiesgo(metrica.getNivelRiesgo().name());
                alerta.setIndicePuntualidad(metrica.getIndicePuntualidad());
                alerta.setFaltasMes(metrica.getFaltasPeriodo());
                alerta.setTardanzasMes(metrica.getTardanzasPeriodo());
                alerta.setPatronDetectado(metrica.getPatronDetectado());
                
                ranking.add(alerta);
            }
        }
        
        // Ordenar por nivel de riesgo (ALTO primero) y luego por índice de puntualidad (menor primero)
        ranking.sort((a, b) -> {
            if (a.getNivelRiesgo().equals("ALTO") && !b.getNivelRiesgo().equals("ALTO")) return -1;
            if (!a.getNivelRiesgo().equals("ALTO") && b.getNivelRiesgo().equals("ALTO")) return 1;
            
            Double indiceA = a.getIndicePuntualidad() != null ? a.getIndicePuntualidad() : 100.0;
            Double indiceB = b.getIndicePuntualidad() != null ? b.getIndicePuntualidad() : 100.0;
            return indiceA.compareTo(indiceB);
        });
        
        // Top 10
        return ranking.stream().limit(10).collect(Collectors.toList());
    }
}