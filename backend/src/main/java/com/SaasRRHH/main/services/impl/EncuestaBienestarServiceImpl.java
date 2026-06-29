package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.EncuestaBienestarRequestDTO;
import com.SaasRRHH.main.DTO.EncuestaBienestarResponseDTO;
import com.SaasRRHH.main.DTO.ResumenBienestarDTO;
import com.SaasRRHH.main.mapper.EncuestaBienestarMapper;
import com.SaasRRHH.main.model.Encuestabienestar;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.MetricaBurnout;
import com.SaasRRHH.main.repository.EmpleadoRepository;
import com.SaasRRHH.main.repository.EncuestaBienestarRepository;
import com.SaasRRHH.main.repository.MetricaBurnoutRepository;
import com.SaasRRHH.main.services.EncuestaBienestarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class EncuestaBienestarServiceImpl implements EncuestaBienestarService {

    private final EncuestaBienestarRepository repository;
    private final EmpleadoRepository empleadoRepository;
    private final MetricaBurnoutRepository metricaBurnoutRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EncuestaBienestarResponseDTO> listar() {
        return repository.findAll().stream()
                .map(EncuestaBienestarMapper::toDTO)
                .toList();
    }

    @Override
    public EncuestaBienestarResponseDTO guardar(EncuestaBienestarRequestDTO encuesta) {
        if (encuesta == null || encuesta.getEmpleadoId() == null) {
            throw new IllegalArgumentException("EmpleadoId es requerido");
        }

        Empleado empleado = empleadoRepository.findById(encuesta.getEmpleadoId())
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));

        LocalDate fecha = encuesta.getFecha() == null ? LocalDate.now() : encuesta.getFecha();

        // Una encuesta por empleado por día
        if (repository.existsByEmpleadoIdAndFecha(empleado.getId(), fecha)) {
            throw new IllegalStateException("El empleado ya registró una encuesta en esa fecha");
        }

        validarValores(encuesta.getCargaLaboral(), encuesta.getApoyoEquipo(), encuesta.getProyeccion());

        Encuestabienestar entity = EncuestaBienestarMapper.toEntity(encuesta);
        entity.setEmpleado(empleado);
        entity.setFecha(fecha);

        Encuestabienestar guardada = repository.save(entity);

        actualizarBurnout(empleado, guardada);

        return EncuestaBienestarMapper.toDTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public EncuestaBienestarResponseDTO obtenerPorId(Long id) {
        return repository.findById(id)
                .map(EncuestaBienestarMapper::toDTO)
                .orElse(null);
    }

    @Override
    public EncuestaBienestarResponseDTO actualizar(Long id, EncuestaBienestarRequestDTO data) {
        Encuestabienestar actual = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Encuesta no encontrada"));

        validarValores(data.getCargaLaboral(), data.getApoyoEquipo(), data.getProyeccion());

        actual.setCargaLaboral(data.getCargaLaboral());
        actual.setApoyoEquipo(data.getApoyoEquipo());
        actual.setProyeccion(data.getProyeccion());

        Encuestabienestar guardada = repository.save(actual);

        // Actualizar burnout del empleado
        if (guardada.getEmpleado() != null) {
            actualizarBurnout(guardada.getEmpleado(), guardada);
        }

        return EncuestaBienestarMapper.toDTO(guardada);
    }

    @Override
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Encuesta no encontrada");
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EncuestaBienestarResponseDTO> obtenerHistorialEmpleado(Long empleadoId) {
        return repository.findByEmpleadoIdOrderByFechaDesc(empleadoId).stream()
                .map(EncuestaBienestarMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EncuestaBienestarResponseDTO> obtenerPorRangoFechas(LocalDate inicio, LocalDate fin) {
        return repository.findByFechaBetween(inicio, fin).stream()
                .map(EncuestaBienestarMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> obtenerEmpleadosEnRiesgo() {
        LocalDate fechaLimite = LocalDate.now().minusDays(7);
        return repository.findEmpleadosEnRiesgo(fechaLimite);
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenBienestarDTO obtenerResumenMensual(LocalDate inicio, LocalDate fin) {
        List<Encuestabienestar> encuestas = repository.findByFechaBetween(inicio, fin);
        ResumenBienestarDTO resumen = new ResumenBienestarDTO();
        if (encuestas.isEmpty()) {
            resumen.setTotalEncuestas(0L);
            resumen.setPromedioGeneral(null);
            resumen.setPromedioCargaLaboral(null);
            resumen.setPromedioApoyoEquipo(null);
            resumen.setPromedioProyeccion(null);
            resumen.setEmpleadosCriticos(0L);
            resumen.setPulsoOrganizacional(null);
            return resumen;
        }

        long total = encuestas.size();
        double sumCarga = encuestas.stream().mapToDouble(e -> e.getCargaLaboral() == null ? 0 : e.getCargaLaboral())
                .sum();
        double sumApoyo = encuestas.stream().mapToDouble(e -> e.getApoyoEquipo() == null ? 0 : e.getApoyoEquipo())
                .sum();
        double sumProy = encuestas.stream().mapToDouble(e -> e.getProyeccion() == null ? 0 : e.getProyeccion()).sum();

        double promCarga = redondear(sumCarga / total);
        double promApoyo = redondear(sumApoyo / total);
        double promProy = redondear(sumProy / total);
        double promGeneral = redondear((promCarga + promApoyo + promProy) / 3.0);

        // Empleados criticos en el rango
        Map<Long, Double> promedioPorEmpleado = encuestas.stream()
                .collect(Collectors.groupingBy(e -> e.getEmpleado().getId(),
                        Collectors.averagingDouble(
                                e -> (e.getCargaLaboral() + e.getApoyoEquipo() + e.getProyeccion()) / 3.0)));

        long empleadosCriticos = promedioPorEmpleado.values().stream().filter(p -> p < 2.5).count();

        resumen.setTotalEncuestas(total);
        resumen.setPromedioCargaLaboral(promCarga);
        resumen.setPromedioApoyoEquipo(promApoyo);
        resumen.setPromedioProyeccion(promProy);
        resumen.setPromedioGeneral(promGeneral);
        resumen.setEmpleadosCriticos(empleadosCriticos);
        resumen.setPulsoOrganizacional(clasificarNivel(promGeneral));

        return resumen;
    }

    // =====================
    // Helpers
    // =====================
    private void validarValores(Integer carga, Integer apoyo, Integer proyeccion) {
        if (carga == null || apoyo == null || proyeccion == null) {
            throw new IllegalArgumentException("Todos los valores deben estar presentes");
        }
        if (carga < 1 || carga > 5 || apoyo < 1 || apoyo > 5 || proyeccion < 1 || proyeccion > 5) {
            throw new IllegalArgumentException("Los valores deben estar entre 1 y 5");
        }
    }

    private void actualizarBurnout(Empleado empleado, Encuestabienestar encuesta) {
        Double promedio = calcularPromedio(encuesta.getCargaLaboral(), encuesta.getApoyoEquipo(),
                encuesta.getProyeccion());
        MetricaBurnout.NivelRiesgoBurnout nivel;
        if (promedio >= 4.0)
            nivel = MetricaBurnout.NivelRiesgoBurnout.BAJO;
        else if (promedio >= 2.5)
            nivel = MetricaBurnout.NivelRiesgoBurnout.MEDIO;
        else
            nivel = MetricaBurnout.NivelRiesgoBurnout.ALTO;

        MetricaBurnout metrica = new MetricaBurnout();
        metrica.setEmpleado(empleado);
        metrica.setNivelRiesgo(nivel);
        metricaBurnoutRepository.save(metrica);
    }

    private Double calcularPromedio(Integer carga, Integer apoyo, Integer proyeccion) {
        if (carga == null || apoyo == null || proyeccion == null)
            return 0.0;
        return redondear((carga + apoyo + proyeccion) / 3.0);
    }

    private Double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    private String clasificarNivel(Double promedio) {
        if (promedio == null)
            return null;
        if (promedio >= 4.0)
            return "BUENO";
        if (promedio >= 2.5)
            return "REGULAR";
        return "CRITICO";
    }
}
