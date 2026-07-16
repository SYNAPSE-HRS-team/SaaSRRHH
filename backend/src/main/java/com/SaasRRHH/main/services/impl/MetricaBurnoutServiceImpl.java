package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.MetricaBurnoutResponseDTO;
import com.SaasRRHH.main.mapper.MetricaBurnoutMapper;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.MetricaBurnout;
import com.SaasRRHH.main.model.RegistroAsistencia;
import com.SaasRRHH.main.model.TareaAsignada;
import com.SaasRRHH.main.model.Encuestabienestar;
import com.SaasRRHH.main.repository.*;
import com.SaasRRHH.main.services.MetricaBurnoutService;
import com.SaasRRHH.main.services.RegistroAsistenciaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MetricaBurnoutServiceImpl implements MetricaBurnoutService {

    private static final Logger log = LoggerFactory.getLogger(MetricaBurnoutServiceImpl.class);

    private final MetricaBurnoutRepository repository;
    private final EmpleadoRepository empleadoRepository;
    private final RegistroAsistenciaRepository asistenciaRepository;
    private final TareaAsignadaRepository tareaRepository;
    private final EncuestaBienestarRepository encuestaRepository;
    private final RegistroAsistenciaService registroAsistenciaService;

    // ============================================
    // CRUD BÁSICO
    // ============================================

    @Override
    @Transactional(readOnly = true)
    public List<MetricaBurnoutResponseDTO> listar() {
        return repository.findAllWithRelaciones()
                .stream()
                .map(MetricaBurnoutMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MetricaBurnoutResponseDTO obtenerPorId(Long id) {
        return repository.findByIdWithRelaciones(id)
                .map(MetricaBurnoutMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Métrica no encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetricaBurnoutResponseDTO> buscarPorEmpleado(Long empleadoId) {
        return repository.findByEmpleadoId(empleadoId)
                .stream()
                .map(MetricaBurnoutMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    // ============================================
    // ✅ CÁLCULO AUTOMÁTICO DE MÉTRICAS (REFACTORIZADO)
    // ============================================

    @Override
    public MetricaBurnoutResponseDTO calcularMetrica(Long empleadoId) {
        log.info("📊 Calculando métrica de burnout para empleado ID: {}", empleadoId);

        // 1. Validar empleado
        var empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + empleadoId));

        // 2. Período de evaluación (últimos 30 días)
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fin = LocalDateTime.now();

        // 3. Obtener registros de asistencia del período
        List<RegistroAsistencia> asistencias = asistenciaRepository
                .findByEmpleadoIdAndFechaHoraBetween(empleadoId, inicio, fin);

        // 4. ✅ NUEVO: Calcular faltas en el período
        int faltas = contarFaltasPeriodo(empleado, inicio.toLocalDate(), fin.toLocalDate(), asistencias);
        log.info("   Faltas en el período: {}", faltas);

        // 5. ✅ NUEVO: Calcular tardanzas en el período
        int tardanzas = contarTardanzasPeriodo(asistencias);
        log.info("   Tardanzas en el período: {}", tardanzas);

        // 6. ✅ NUEVO: Detectar patrón de comportamiento
        String patron = registroAsistenciaService.detectarPatronTardanza(empleadoId);
        log.info("   Patrón detectado: {}", patron);

        // 7. ✅ NUEVO: Calcular índice de puntualidad (0-100)
        double indicePuntualidad = calcularIndicePuntualidad(empleado, inicio.toLocalDate(), fin.toLocalDate(), asistencias);
        log.info("   Índice de puntualidad: {}", indicePuntualidad);

        // 8. ✅ NUEVO: Calcular horas reales vs contrato
        long[] horasCalculadas = calcularHorasRealesVsContrato(empleado, inicio.toLocalDate(), fin.toLocalDate(), asistencias);
        int horasReales = (int) horasCalculadas[0];
        int horasContrato = (int) horasCalculadas[1];
        int horasExtra = Math.max(0, horasReales - horasContrato);
        log.info("   Horas reales: {}, Horas contrato: {}, Horas extra: {}", horasReales, horasContrato, horasExtra);

        // 9. ✅ NUEVO: Días trabajados
        int diasTrabajados = contarDiasTrabajados(asistencias);

        // 10. Calcular horas extra por tareas (método original)
        LocalDate inicioTareas = LocalDate.now().minusDays(30);
        List<TareaAsignada> tareas = tareaRepository.findByEmpleadoIdAndFechaBetween(
                empleadoId, inicioTareas, LocalDate.now()
        );
        int horasExtraTareas = calcularHorasExtra(tareas);

        // 11. Obtener promedio de encuestas recientes
        LocalDate inicioEncuestas = LocalDate.now().minusDays(30);
        List<Encuestabienestar> encuestas = encuestaRepository
                .findByEmpleadoIdAndFechaBetween(empleadoId, inicioEncuestas, LocalDate.now());
        double promedioEncuestas = calcularPromedioEncuestas(encuestas);

        // 12. ✅ NUEVO: Calcular nivel de riesgo mejorado
        boolean tendenciaTardanza = tardanzas > 3;
        MetricaBurnout.NivelRiesgoBurnout nivel = calcularNivelRiesgoMejorado(
                horasExtra + horasExtraTareas,
                tardanzas,
                faltas,
                indicePuntualidad,
                promedioEncuestas,
                patron
        );
        log.info("   Nivel de riesgo: {}", nivel);

        // 13. Buscar si ya existe una métrica del empleado en el mes actual, o crear una nueva
        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime finMes = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(LocalTime.MAX);
        
        Optional<MetricaBurnout> metricaExistente = repository.findFirstByEmpleadoIdAndFechaEvaluacionBetween(empleadoId, inicioMes, finMes);
        
        MetricaBurnout metrica = metricaExistente.orElseGet(() -> {
            MetricaBurnout newMetrica = new MetricaBurnout();
            newMetrica.setEmpleado(empleado);
            return newMetrica;
        });

        metrica.setNivelRiesgo(nivel);
        metrica.setHorasExtraAcumuladas(horasExtra + horasExtraTareas);
        metrica.setTendenciaTardanza(tendenciaTardanza);
        metrica.setFechaEvaluacion(LocalDateTime.now());
        // ✅ Nuevos campos
        metrica.setFaltasPeriodo(faltas);
        metrica.setTardanzasPeriodo(tardanzas);
        metrica.setPatronDetectado(patron);
        metrica.setIndicePuntualidad(indicePuntualidad);
        metrica.setDiasTrabajados(diasTrabajados);
        metrica.setHorasReales(horasReales);
        metrica.setHorasContrato(horasContrato);

        MetricaBurnout saved = repository.save(metrica);
        log.info("✅ Métrica guardada con ID: {}", saved.getId());

        return MetricaBurnoutMapper.toDTO(saved);
    }

    @Override
    public List<MetricaBurnoutResponseDTO> recalcularTodas() {
        log.info("📊 Recalculando métricas para todos los empleados");

        List<Long> empleados = empleadoRepository.findByActivoTrue().stream()
                .map(e -> e.getId())
                .collect(Collectors.toList());

        return empleados.stream()
                .map(this::calcularMetrica)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetricaBurnoutResponseDTO> obtenerHistorialCompleto(Long empleadoId) {
        return repository.findByEmpleadoId(empleadoId)
                .stream()
                .map(MetricaBurnoutMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public String obtenerUltimoNivelRiesgo(Long empleadoId) {
        return repository.findByEmpleadoId(empleadoId)
                .stream()
                .findFirst()
                .map(m -> m.getNivelRiesgo().name())
                .orElse("SIN_EVALUACION");
    }

    // ============================================
    // ⏰ TAREA PROGRAMADA - Ejecución automática
    // ============================================

    @Scheduled(cron = "0 0 6 * * ?")
    public void ejecutarCalculoAutomatico() {
        log.info("⏰ Ejecutando cálculo automático de métricas de burnout");
        try {
            recalcularTodas();
            log.info("✅ Cálculo automático completado exitosamente");
        } catch (Exception e) {
            log.error("❌ Error en cálculo automático: {}", e.getMessage(), e);
        }
    }

    // ============================================
    // ✅ NUEVA LÓGICA DE CÁLCULO
    // ============================================

    /**
     * Cuenta las faltas en el período basado en días laborables sin registro
     */
    private int contarFaltasPeriodo(Empleado empleado, LocalDate inicio, LocalDate fin, List<RegistroAsistencia> asistencias) {
        int faltas = 0;
        LocalDate fecha = inicio;
        
        while (!fecha.isAfter(fin)) {
            if (empleado.getFechaInicioContrato() != null && fecha.isBefore(empleado.getFechaInicioContrato())) {
                fecha = fecha.plusDays(1);
                continue;
            }
            if (empleado.esDiaLaborable(fecha.getDayOfWeek())) {
                LocalDate fechaFinal = fecha;
                boolean marcoEntrada = asistencias.stream()
                        .anyMatch(a -> a.getFechaHora().toLocalDate().equals(fechaFinal) 
                                && "ENTRADA".equals(a.getTipoMarcacion())
                                && !"RECHAZADO".equals(a.getEstado()));
                if (!marcoEntrada) {
                    faltas++;
                }
            }
            fecha = fecha.plusDays(1);
        }
        
        // También contar faltas registradas por el sistema
        long faltasSistema = asistencias.stream()
                .filter(a -> a.getEsFalta() != null && a.getEsFalta())
                .filter(a -> empleado.getFechaInicioContrato() == null || !a.getFechaHora().toLocalDate().isBefore(empleado.getFechaInicioContrato()))
                .count();
        
        return Math.max(faltas, (int) faltasSistema);
    }

    /**
     * Cuenta las tardanzas en el período
     */
    private int contarTardanzasPeriodo(List<RegistroAsistencia> asistencias) {
        return (int) asistencias.stream()
                .filter(a -> "ENTRADA".equals(a.getTipoMarcacion()))
                .filter(a -> a.getMinutosTardanza() != null && a.getMinutosTardanza() > 0)
                .count();
    }

    /**
     * Calcula el índice de puntualidad (0-100)
     */
    private double calcularIndicePuntualidad(Empleado empleado, LocalDate inicio, LocalDate fin, List<RegistroAsistencia> asistencias) {
        List<RegistroAsistencia> entradas = asistencias.stream()
                .filter(a -> "ENTRADA".equals(a.getTipoMarcacion()))
                .filter(a -> !"RECHAZADO".equals(a.getEstado()))
                .collect(Collectors.toList());
        
        if (entradas.isEmpty()) return 0.0;
        
        long puntuales = entradas.stream()
                .filter(a -> a.getMinutosTardanza() == null || a.getMinutosTardanza() == 0)
                .count();
        
        return (double) puntuales / entradas.size() * 100.0;
    }

    /**
     * Calcula horas reales trabajadas vs horas de contrato
     * Retorna [horasReales, horasContrato]
     */
    private long[] calcularHorasRealesVsContrato(Empleado empleado, LocalDate inicio, LocalDate fin, List<RegistroAsistencia> asistencias) {
        long horasReales = 0;
        long horasContrato = 0;
        long horasPorDia = empleado.horasContratoPorDia();
        
        LocalDate fecha = inicio;
        while (!fecha.isAfter(fin)) {
            if (empleado.getFechaInicioContrato() != null && fecha.isBefore(empleado.getFechaInicioContrato())) {
                fecha = fecha.plusDays(1);
                continue;
            }
            if (empleado.esDiaLaborable(fecha.getDayOfWeek())) {
                horasContrato += horasPorDia;
                
                LocalDate fechaFinal = fecha;
                Optional<RegistroAsistencia> entrada = asistencias.stream()
                        .filter(a -> a.getFechaHora().toLocalDate().equals(fechaFinal) 
                                && "ENTRADA".equals(a.getTipoMarcacion())
                                && !"RECHAZADO".equals(a.getEstado()))
                        .findFirst();
                Optional<RegistroAsistencia> salida = asistencias.stream()
                        .filter(a -> a.getFechaHora().toLocalDate().equals(fechaFinal) 
                                && "SALIDA".equals(a.getTipoMarcacion())
                                && !"RECHAZADO".equals(a.getEstado()))
                        .findFirst();
                
                if (entrada.isPresent() && salida.isPresent()) {
                    horasReales += ChronoUnit.HOURS.between(
                        entrada.get().getFechaHora(), 
                        salida.get().getFechaHora()
                    );
                } else if (entrada.isPresent()) {
                    // Si no hay salida, asumir que trabajó las horas completas
                    horasReales += horasPorDia;
                }
            }
            fecha = fecha.plusDays(1);
        }
        
        return new long[]{horasReales, horasContrato};
    }

    /**
     * Cuenta días efectivamente trabajados
     */
    private int contarDiasTrabajados(List<RegistroAsistencia> asistencias) {
        return (int) asistencias.stream()
                .filter(a -> "ENTRADA".equals(a.getTipoMarcacion()))
                .filter(a -> !"RECHAZADO".equals(a.getEstado()))
                .map(a -> a.getFechaHora().toLocalDate())
                .distinct()
                .count();
    }

    // ============================================
    // MÉTODOS ORIGINALES
    // ============================================

    private int calcularHorasExtra(List<TareaAsignada> tareas) {
        int horas = 0;
        for (TareaAsignada t : tareas) {
            if (t.getEstado() == TareaAsignada.EstadoTarea.INCONCLUSO) {
                horas += 8;
            }
            if (t.getEstado() == TareaAsignada.EstadoTarea.CANCELADO) {
                horas += 4;
            }
            if (t.getEstado() == TareaAsignada.EstadoTarea.PENDIENTE &&
                    t.getFecha().isBefore(LocalDate.now())) {
                horas += 2;
            }
        }
        return Math.min(horas, 100);
    }

    private double calcularPromedioEncuestas(List<Encuestabienestar> encuestas) {
        if (encuestas.isEmpty()) return 3.0;

        double promedio = encuestas.stream()
                .mapToDouble(e -> {
                    int carga = e.getCargaLaboral() != null ? e.getCargaLaboral() : 3;
                    int apoyo = e.getApoyoEquipo() != null ? e.getApoyoEquipo() : 3;
                    int proyeccion = e.getProyeccion() != null ? e.getProyeccion() : 3;
                    return (carga + apoyo + proyeccion) / 3.0;
                })
                .average()
                .orElse(3.0);

        return promedio;
    }

    /**
     * ✅ NUEVO: Cálculo de nivel de riesgo mejorado
     */
    private MetricaBurnout.NivelRiesgoBurnout calcularNivelRiesgoMejorado(
            int horasExtra,
            int tardanzas,
            int faltas,
            double indicePuntualidad,
            double promedioEncuestas,
            String patron
    ) {
        int puntaje = 0;

        // 1. Horas extra
        if (horasExtra > 60) puntaje += 30;
        else if (horasExtra > 40) puntaje += 20;
        else if (horasExtra > 20) puntaje += 10;

        // 2. Tardanzas
        if (tardanzas > 5) puntaje += 25;
        else if (tardanzas > 3) puntaje += 15;
        else if (tardanzas > 0) puntaje += 5;

        // 3. Faltas
        if (faltas > 5) puntaje += 25;
        else if (faltas > 3) puntaje += 15;
        else if (faltas > 1) puntaje += 5;

        // 4. Índice de puntualidad
        if (indicePuntualidad < 50) puntaje += 20;
        else if (indicePuntualidad < 75) puntaje += 10;

        // 5. Patrón detectado (bonificación de riesgo)
        if (patron != null && !patron.isEmpty()) {
            puntaje += 15;
            log.info("   ⚠️ Patrón detectado: +15 puntos");
        }

        // 6. Encuestas
        if (promedioEncuestas <= 2.0) puntaje += 20;
        else if (promedioEncuestas <= 3.0) puntaje += 10;

        log.info("   Puntaje total: {}", puntaje);

        if (puntaje >= 60) return MetricaBurnout.NivelRiesgoBurnout.ALTO;
        else if (puntaje >= 35) return MetricaBurnout.NivelRiesgoBurnout.MEDIO;
        else return MetricaBurnout.NivelRiesgoBurnout.BAJO;
    }
}