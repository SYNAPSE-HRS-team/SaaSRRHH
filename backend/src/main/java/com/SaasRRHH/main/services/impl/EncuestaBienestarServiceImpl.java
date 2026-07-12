package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.EncuestaBienestarRequestDTO;
import com.SaasRRHH.main.DTO.EncuestaBienestarResponseDTO;
import com.SaasRRHH.main.DTO.ResumenBienestarDTO;
import com.SaasRRHH.main.mapper.EncuestaBienestarMapper;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.Encuestabienestar;
import com.SaasRRHH.main.model.MetricaBurnout;
import com.SaasRRHH.main.repository.EmpleadoRepository;
import com.SaasRRHH.main.repository.EncuestaBienestarRepository;
import com.SaasRRHH.main.repository.MetricaBurnoutRepository;
import com.SaasRRHH.main.services.EncuestaBienestarService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EncuestaBienestarServiceImpl implements EncuestaBienestarService {

    private static final Logger log = LoggerFactory.getLogger(EncuestaBienestarServiceImpl.class);

    private final EncuestaBienestarRepository repository;
    private final EmpleadoRepository empleadoRepository;
    private final MetricaBurnoutRepository metricaBurnoutRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EncuestaBienestarResponseDTO> listar() {
        return repository.findAll().stream()
                .map(EncuestaBienestarMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EncuestaBienestarResponseDTO obtenerPorId(Long id) {
        Encuestabienestar encuesta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Encuesta no encontrada con ID: " + id));
        return EncuestaBienestarMapper.toDTO(encuesta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EncuestaBienestarResponseDTO> obtenerHistorialEmpleado(Long empleadoId) {
        return repository.findByEmpleadoIdOrderByFechaDesc(empleadoId).stream()
                .map(EncuestaBienestarMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EncuestaBienestarResponseDTO> obtenerPorRangoFechas(LocalDate inicio, LocalDate fin) {
        return repository.findByFechaBetween(inicio, fin).stream()
                .map(EncuestaBienestarMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> obtenerEmpleadosEnRiesgo() {
        return repository.findEmpleadosEnRiesgo(LocalDate.now().minusDays(30));
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenBienestarDTO obtenerResumenMensual(LocalDate inicio, LocalDate fin) {
        List<Encuestabienestar> encuestas = repository.findByFechaBetween(inicio, fin);
        
        ResumenBienestarDTO resumen = new ResumenBienestarDTO();
        resumen.setTotalEncuestas((long) encuestas.size());
        
        if (!encuestas.isEmpty()) {
            double promCarga = encuestas.stream()
                    .mapToInt(e -> e.getCargaLaboral() != null ? e.getCargaLaboral() : 3)
                    .average().orElse(3.0);
            double promApoyo = encuestas.stream()
                    .mapToInt(e -> e.getApoyoEquipo() != null ? e.getApoyoEquipo() : 3)
                    .average().orElse(3.0);
            double promProyeccion = encuestas.stream()
                    .mapToInt(e -> e.getProyeccion() != null ? e.getProyeccion() : 3)
                    .average().orElse(3.0);
            
            resumen.setPromedioCargaLaboral(Math.round(promCarga * 100.0) / 100.0);
            resumen.setPromedioApoyoEquipo(Math.round(promApoyo * 100.0) / 100.0);
            resumen.setPromedioProyeccion(Math.round(promProyeccion * 100.0) / 100.0);
            resumen.setPromedioGeneral(Math.round((promCarga + promApoyo + promProyeccion) / 3.0 * 100.0) / 100.0);
            
            long criticos = encuestas.stream()
                    .filter(e -> e.getCargaLaboral() != null && e.getCargaLaboral() >= 4)
                    .count();
            resumen.setEmpleadosCriticos(criticos);
            
            if (resumen.getPromedioGeneral() >= 3.5) {
                resumen.setPulsoOrganizacional("CRITICO");
            } else if (resumen.getPromedioGeneral() >= 2.5) {
                resumen.setPulsoOrganizacional("REGULAR");
            } else {
                resumen.setPulsoOrganizacional("SALUDABLE");
            }
        }
        
        return resumen;
    }

    @Override
    public EncuestaBienestarResponseDTO guardar(EncuestaBienestarRequestDTO dto) {
        Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        
        Encuestabienestar encuesta = EncuestaBienestarMapper.toEntity(dto, empleado);
        
        // ✅ NUEVO: Si la encuesta indica nivel alto de estrés, generar alerta
        if (dto.getCargaLaboral() != null && dto.getCargaLaboral() >= 4) {
            log.warn("⚠️ Encuesta con carga laboral alta para empleado: {} {}", 
                     empleado.getNombres(), empleado.getApellidos());
            encuesta.setNivelBienestar("CRITICO");
        } else if (dto.getCargaLaboral() != null && dto.getCargaLaboral() >= 3) {
            encuesta.setNivelBienestar("REGULAR");
        } else {
            encuesta.setNivelBienestar("BUENO");
        }
        
        if (dto.getApoyoEquipo() != null && dto.getApoyoEquipo() <= 2) {
            log.warn("⚠️ Encuesta con bajo apoyo de equipo para empleado: {} {}", 
                     empleado.getNombres(), empleado.getApellidos());
        }
        
        Encuestabienestar guardada = repository.save(encuesta);
        
        log.info("📊 Encuesta guardada para empleado #{} - Se recomienda recalcular métricas de burnout", 
                 empleado.getId());
        
        return EncuestaBienestarMapper.toDTO(guardada);
    }

    @Override
    public EncuestaBienestarResponseDTO actualizar(Long id, EncuestaBienestarRequestDTO dto) {
        Encuestabienestar encuesta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Encuesta no encontrada con ID: " + id));
        
        Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        
        encuesta.setEmpleado(empleado);
        encuesta.setCargaLaboral(dto.getCargaLaboral());
        encuesta.setApoyoEquipo(dto.getApoyoEquipo());
        encuesta.setProyeccion(dto.getProyeccion());
        encuesta.setFecha(dto.getFecha());
        
        // Actualizar nivel de bienestar
        if (dto.getCargaLaboral() != null && dto.getCargaLaboral() >= 4) {
            encuesta.setNivelBienestar("CRITICO");
        } else if (dto.getCargaLaboral() != null && dto.getCargaLaboral() >= 3) {
            encuesta.setNivelBienestar("REGULAR");
        } else {
            encuesta.setNivelBienestar("BUENO");
        }
        
        Encuestabienestar actualizada = repository.save(encuesta);
        log.info("✅ Encuesta #{} actualizada para empleado: {} {}", 
                 id, empleado.getNombres(), empleado.getApellidos());
        
        return EncuestaBienestarMapper.toDTO(actualizada);
    }

    @Override
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Encuesta no encontrada con ID: " + id);
        }
        repository.deleteById(id);
        log.info("🗑️ Encuesta #{} eliminada", id);
    }

    // ============================================
    // ✅ NUEVOS MÉTODOS DE INTEGRACIÓN CON BURNOUT
    // ============================================

    @Override
    public String evaluarRiesgoPorEncuestas(Long empleadoId) {
        List<Encuestabienestar> encuestas = repository.findByEmpleadoIdAndFechaBetween(
                empleadoId, LocalDate.now().minusDays(30), LocalDate.now());
        
        if (encuestas.isEmpty()) {
            return "SIN_DATOS";
        }
        
        double promedioCarga = encuestas.stream()
                .mapToInt(e -> e.getCargaLaboral() != null ? e.getCargaLaboral() : 3)
                .average()
                .orElse(3.0);
        
        double promedioApoyo = encuestas.stream()
                .mapToInt(e -> e.getApoyoEquipo() != null ? e.getApoyoEquipo() : 3)
                .average()
                .orElse(3.0);
        
        if (promedioCarga >= 4 && promedioApoyo <= 2) {
            log.warn("⚠️ Empleado #{} con riesgo ALTO según encuestas", empleadoId);
            return "ALTO";
        } else if (promedioCarga >= 3 || promedioApoyo <= 3) {
            return "MEDIO";
        } else {
            return "BAJO";
        }
    }

    @Override
    public String compararConBurnout(Long empleadoId) {
        String riesgoEncuestas = evaluarRiesgoPorEncuestas(empleadoId);
        
        List<MetricaBurnout> metricas = metricaBurnoutRepository.findByEmpleadoId(empleadoId);
        String riesgoBurnout = metricas.stream()
                .findFirst()
                .map(m -> m.getNivelRiesgo().name())
                .orElse("SIN_EVALUACION");
        
        log.info("📊 Empleado #{} - Riesgo encuestas: {} - Riesgo burnout: {}", 
                 empleadoId, riesgoEncuestas, riesgoBurnout);
        
        if ("ALTO".equals(riesgoEncuestas) && "ALTO".equals(riesgoBurnout)) {
            return "CRITICO - Encuestas y métricas indican riesgo alto";
        } else if ("ALTO".equals(riesgoEncuestas) || "ALTO".equals(riesgoBurnout)) {
            return "ALTO - Un indicador muestra riesgo elevado";
        } else if ("MEDIO".equals(riesgoEncuestas) || "MEDIO".equals(riesgoBurnout)) {
            return "MEDIO - Seguimiento recomendado";
        }
        
        return "BAJO - Sin alertas significativas";
    }
}