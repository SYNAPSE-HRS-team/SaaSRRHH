package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.MetricaBurnoutResponseDTO;
import com.SaasRRHH.main.mapper.MetricaBurnoutMapper;
import com.SaasRRHH.main.model.MetricaBurnout;
import com.SaasRRHH.main.model.RegistroAsistencia;
import com.SaasRRHH.main.model.TareaAsignada;
import com.SaasRRHH.main.model.Encuestabienestar;
import com.SaasRRHH.main.repository.*;
import com.SaasRRHH.main.services.MetricaBurnoutService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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
    // ✅ CÁLCULO AUTOMÁTICO DE MÉTRICAS
    // ============================================

    @Override
    public MetricaBurnoutResponseDTO calcularMetrica(Long empleadoId) {
        log.info("📊 Calculando métrica de burnout para empleado ID: {}", empleadoId);

        // 1. Validar empleado
        var empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + empleadoId));

        // 2. Calcular horas extra (últimos 30 días)
        LocalDate inicioTareas = LocalDate.now().minusDays(30);
        List<TareaAsignada> tareas = tareaRepository.findByEmpleadoIdAndFechaBetween(
                empleadoId, inicioTareas, LocalDate.now()
        );
        int horasExtra = calcularHorasExtra(tareas);
        log.info("   Horas extra acumuladas: {}", horasExtra);

        // 3. Calcular tendencia de tardanza (últimos 15 días)
        LocalDateTime inicioAsistencias = LocalDateTime.now().minusDays(15);
        LocalDateTime finAsistencias = LocalDateTime.now();
        List<RegistroAsistencia> asistencias = asistenciaRepository
                .findByEmpleadoIdAndFechaHoraBetween(empleadoId, inicioAsistencias, finAsistencias);
        boolean tendenciaTardanza = calcularTendenciaTardanza(asistencias);
        log.info("   Tendencia a tardanzas: {}", tendenciaTardanza);

        // 4. Obtener promedio de encuestas recientes (últimos 30 días)
        LocalDate inicioEncuestas = LocalDate.now().minusDays(30);
        List<Encuestabienestar> encuestas = encuestaRepository
                .findByEmpleadoIdAndFechaBetween(empleadoId, inicioEncuestas, LocalDate.now());
        double promedioEncuestas = calcularPromedioEncuestas(encuestas);
        log.info("   Promedio encuestas (últimos 30 días): {}", promedioEncuestas);

        // 5. Calcular nivel de riesgo
        MetricaBurnout.NivelRiesgoBurnout nivel = calcularNivelRiesgo(
                horasExtra,
                tendenciaTardanza,
                promedioEncuestas
        );
        log.info("   Nivel de riesgo: {}", nivel);

        // 6. Crear y guardar la métrica
        MetricaBurnout metrica = new MetricaBurnout();
        metrica.setEmpleado(empleado);
        metrica.setNivelRiesgo(nivel);
        metrica.setHorasExtraAcumuladas(horasExtra);
        metrica.setTendenciaTardanza(tendenciaTardanza);
        metrica.setFechaEvaluacion(LocalDateTime.now());

        MetricaBurnout saved = repository.save(metrica);
        log.info("✅ Métrica guardada con ID: {}", saved.getId());

        return MetricaBurnoutMapper.toDTO(saved);
    }

    @Override
    public List<MetricaBurnoutResponseDTO> recalcularTodas() {
        log.info("📊 Recalculando métricas para todos los empleados");

        List<Long> empleados = empleadoRepository.findAll().stream()
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
    // LÓGICA DE CÁLCULO PRIVADA
    // ============================================

    private int calcularHorasExtra(List<TareaAsignada> tareas) {
        int horas = 0;
        for (TareaAsignada t : tareas) {
            if (t.getEstado() == TareaAsignada.EstadoTarea.INCONCLUSO) {
                horas += 8;
                log.debug("      Tarea INCONCLUSO: +8h");
            }
            if (t.getEstado() == TareaAsignada.EstadoTarea.CANCELADO) {
                horas += 4;
                log.debug("      Tarea CANCELADO: +4h");
            }
            if (t.getEstado() == TareaAsignada.EstadoTarea.PENDIENTE &&
                    t.getFecha().isBefore(LocalDate.now())) {
                horas += 2;
                log.debug("      Tarea PENDIENTE atrasada: +2h");
            }
            if (t.getEstado() == TareaAsignada.EstadoTarea.COMPLETADO) {
                horas += 0;
                log.debug("      Tarea COMPLETADO: +0h");
            }
        }
        return Math.min(horas, 100);
    }

    private boolean calcularTendenciaTardanza(List<RegistroAsistencia> asistencias) {
        // Verificar si hay registros con estado "OBSERVADO" (tardanza)
        long tardanzas = asistencias.stream()
                .filter(a -> a.getEstado() != null && "OBSERVADO".equals(a.getEstado()))
                .count();
        log.debug("      Tardanzas en últimos 15 días: {}", tardanzas);
        return tardanzas > 3;
    }

    private double calcularPromedioEncuestas(List<Encuestabienestar> encuestas) {
        if (encuestas.isEmpty()) {
            log.debug("      Sin encuestas, promedio base: 3.0");
            return 3.0;
        }

        double promedio = encuestas.stream()
                .mapToDouble(e -> {
                    int carga = e.getCargaLaboral() != null ? e.getCargaLaboral() : 3;
                    int apoyo = e.getApoyoEquipo() != null ? e.getApoyoEquipo() : 3;
                    int proyeccion = e.getProyeccion() != null ? e.getProyeccion() : 3;
                    return (carga + apoyo + proyeccion) / 3.0;
                })
                .average()
                .orElse(3.0);

        log.debug("      Promedio de encuestas ({} encuestas): {}", encuestas.size(), promedio);
        return promedio;
    }

    private MetricaBurnout.NivelRiesgoBurnout calcularNivelRiesgo(
            int horasExtra,
            boolean tendenciaTardanza,
            double promedioEncuestas
    ) {
        int puntaje = 0;

        // 1. Horas extra (máximo 30 puntos)
        if (horasExtra > 60) {
            puntaje += 30;
            log.debug("      +30 puntos por horas extra altas");
        } else if (horasExtra > 40) {
            puntaje += 20;
            log.debug("      +20 puntos por horas extra moderadas");
        } else if (horasExtra > 20) {
            puntaje += 10;
            log.debug("      +10 puntos por horas extra bajas");
        }

        // 2. Tardanzas (máximo 20 puntos)
        if (tendenciaTardanza) {
            puntaje += 20;
            log.debug("      +20 puntos por tendencia a tardanzas");
        }

        // 3. Encuestas (1-5 -> 0-20 puntos)
        if (promedioEncuestas > 4.0) {
            puntaje += 20;
            log.debug("      +20 puntos por encuestas críticas ({}),", promedioEncuestas);
        } else if (promedioEncuestas > 3.0) {
            puntaje += 15;
            log.debug("      +15 puntos por encuestas altas ({})", promedioEncuestas);
        } else if (promedioEncuestas > 2.0) {
            puntaje += 10;
            log.debug("      +10 puntos por encuestas medias ({})", promedioEncuestas);
        } else {
            puntaje += 5;
            log.debug("      +5 puntos por encuestas buenas ({})", promedioEncuestas);
        }

        log.info("   Puntaje total: {}", puntaje);

        // Determinar nivel de riesgo
        if (puntaje >= 50) {
            return MetricaBurnout.NivelRiesgoBurnout.ALTO;
        } else if (puntaje >= 30) {
            return MetricaBurnout.NivelRiesgoBurnout.MEDIO;
        } else {
            return MetricaBurnout.NivelRiesgoBurnout.BAJO;
        }
    }
}