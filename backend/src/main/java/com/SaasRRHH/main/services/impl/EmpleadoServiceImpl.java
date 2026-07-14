package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.EmpleadoRequestDTO;
import com.SaasRRHH.main.DTO.EmpleadoResponseDTO;
import com.SaasRRHH.main.mapper.EmpleadoMapper;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.RegistroAsistencia;
import com.SaasRRHH.main.model.Usuario;
import com.SaasRRHH.main.repository.EmpleadoRepository;
import com.SaasRRHH.main.repository.RegistroAsistenciaRepository;
import com.SaasRRHH.main.repository.UsuarioRepository;
import com.SaasRRHH.main.services.EmpleadoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmpleadoServiceImpl implements EmpleadoService {

    private static final Logger log = LoggerFactory.getLogger(EmpleadoServiceImpl.class);

    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RegistroAsistenciaRepository registroAsistenciaRepository;

    // ============================================
    // MÉTODOS BÁSICOS
    // ============================================

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponseDTO> listar() {
        return empleadoRepository.findAll().stream()
                .map(EmpleadoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponseDTO> listarActivos() {
        return empleadoRepository.findByActivoTrue().stream()
                .map(EmpleadoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmpleadoResponseDTO buscarPorId(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + id));
        EmpleadoResponseDTO dto = EmpleadoMapper.toDTO(empleado);
        enriquecerConMetricas(dto, empleado);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public EmpleadoResponseDTO buscarPorDni(String dni) {
        Empleado empleado = empleadoRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con DNI: " + dni));
        return EmpleadoMapper.toDTO(empleado);
    }

    @Override
    @Transactional(readOnly = true)
    public EmpleadoResponseDTO buscarPorUsuarioId(Long userId) {
        Empleado empleado = empleadoRepository.findByUsuarioId(userId)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado para el usuario ID: " + userId));
        return EmpleadoMapper.toDTO(empleado);
    }

    @Override
    public EmpleadoResponseDTO guardar(EmpleadoRequestDTO dto) {
        validarHorarioLaboral(dto);
        
        Usuario usuario = null;
        if (dto.getUsuarioId() != null) {
            usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        }
        
        Empleado empleado = EmpleadoMapper.toEntity(dto, usuario);
        validarTipoPago(empleado);
        
        Empleado guardado = empleadoRepository.save(empleado);
        log.info("✅ Empleado creado: {} {} - Horario: {} a {} - Tipo pago: {}", 
                 guardado.getNombres(), guardado.getApellidos(),
                 guardado.getHoraEntrada(), guardado.getHoraSalida(),
                 guardado.getTipoPago());
        
        return EmpleadoMapper.toDTO(guardado);
    }

    @Override
    public EmpleadoResponseDTO actualizar(Long id, EmpleadoRequestDTO dto) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + id));

        if (dto.getHoraEntrada() != null || dto.getHoraSalida() != null) {
            validarHorarioLaboral(dto);
        }

        EmpleadoMapper.updateEntity(empleado, dto);

        if (dto.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            empleado.setUsuario(usuario);

            if (dto.getNombres() != null) {
                usuario.setNombre(dto.getNombres());
            }
            if (dto.getApellidos() != null) {
                usuario.setApellido(dto.getApellidos());
            }
            usuarioRepository.save(usuario);
        }

        validarTipoPago(empleado);

        Empleado actualizado = empleadoRepository.save(empleado);
        log.info("✅ Empleado actualizado: {} {}", actualizado.getNombres(), actualizado.getApellidos());

        return EmpleadoMapper.toDTO(actualizado);
    }

    @Override
    public void eliminar(Long id) {
        if (!empleadoRepository.existsById(id)) {
            throw new RuntimeException("Empleado no encontrado con ID: " + id);
        }
        empleadoRepository.deleteById(id);
        log.info("🗑️ Empleado #{} eliminado", id);
    }

    // ============================================
    // CONSULTAS JPQL (MÉTODOS FALTANTES)
    // ============================================

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponseDTO> listarSupervisores() {
        return empleadoRepository.findByCargoContainingIgnoreCase("supervisor").stream()
                .map(EmpleadoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponseDTO> buscarPorCargo(String cargo) {
        return empleadoRepository.findByCargoContainingIgnoreCase(cargo).stream()
                .map(EmpleadoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponseDTO> buscarPorCargoYActivo(String cargo, Boolean activo) {
        return empleadoRepository.findAll().stream()
                .filter(e -> e.getCargo() != null && e.getCargo().toLowerCase().contains(cargo.toLowerCase()))
                .filter(e -> e.getActivo() != null && e.getActivo().equals(activo))
                .map(EmpleadoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponseDTO> listarActivosConUsuario() {
        return empleadoRepository.findActivosConHorario().stream()
                .map(EmpleadoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponseDTO> contratosVencidos() {
        return empleadoRepository.findContratosVencidos(LocalDate.now()).stream()
                .map(EmpleadoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponseDTO> contratosPorVencer(LocalDate fechaLimite) {
        return empleadoRepository.findByFechaInicioContratoBetween(LocalDate.now(), fechaLimite).stream()
                .filter(e -> e.getFechaFinContrato() != null && !e.getFechaFinContrato().isBefore(LocalDate.now()))
                .map(EmpleadoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> contarEmpleadosPorCargo() {
        return empleadoRepository.findAll().stream()
                .filter(e -> e.getCargo() != null)
                .collect(Collectors.groupingBy(Empleado::getCargo, Collectors.counting()))
                .entrySet().stream()
                .map(e -> new Object[]{e.getKey(), e.getValue()})
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponseDTO> listarTrabajadores() {
        return empleadoRepository.findByActivoTrue().stream()
                .filter(e -> e.getCargo() != null && 
                        (e.getCargo().toLowerCase().contains("trabajador") || 
                         e.getCargo().toLowerCase().contains("empleado")))
                .map(EmpleadoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponseDTO> listarTrabajadoresByRol() {
        return empleadoRepository.findByActivoTrue().stream()
                .filter(e -> e.getUsuario() != null && e.getUsuario().getRol() != null &&
                        "TRABAJADOR".equalsIgnoreCase(e.getUsuario().getRol().getNombreRol()))
                .map(EmpleadoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponseDTO> listarSupervisoresByRol() {
        return empleadoRepository.findByActivoTrue().stream()
                .filter(e -> e.getUsuario() != null && e.getUsuario().getRol() != null &&
                        "SUPERVISOR".equalsIgnoreCase(e.getUsuario().getRol().getNombreRol()))
                .map(EmpleadoMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ============================================
    // NUEVOS MÉTODOS DE VALIDACIÓN Y CÁLCULO
    // ============================================

    private void validarHorarioLaboral(EmpleadoRequestDTO dto) {
        if (dto.getHoraEntrada() != null && dto.getHoraSalida() != null) {
            if (!dto.getHoraSalida().isAfter(dto.getHoraEntrada())) {
                throw new RuntimeException("La hora de salida debe ser posterior a la hora de entrada");
            }
            long horas = ChronoUnit.HOURS.between(dto.getHoraEntrada(), dto.getHoraSalida());
            if (horas < 1 || horas > 16) {
                throw new RuntimeException("La jornada laboral debe ser entre 1 y 16 horas");
            }
        }
        if (dto.getToleranciaMinutos() != null && (dto.getToleranciaMinutos() < 0 || dto.getToleranciaMinutos() > 60)) {
            throw new RuntimeException("La tolerancia debe ser entre 0 y 60 minutos");
        }
        if (dto.getDiasLaborables() != null && !dto.getDiasLaborables().isBlank()) {
            String[] dias = dto.getDiasLaborables().split(",");
            for (String dia : dias) {
                String diaTrim = dia.trim().toUpperCase();
                if (!diaTrim.matches("LUN|MAR|MIE|JUE|VIE|SAB|DOM")) {
                    throw new RuntimeException("Día laborable inválido: " + diaTrim);
                }
            }
        }
    }

    private void validarTipoPago(Empleado empleado) {
        if (empleado.getTipoPago() != null) {
            String tipo = empleado.getTipoPago().toUpperCase();
            if (!tipo.matches("HORA|DIA|MENSUAL")) {
                throw new RuntimeException("Tipo de pago inválido. Use: HORA, DIA o MENSUAL");
            }
            empleado.setTipoPago(tipo);
        }
        if (empleado.getMontoPago() != null && empleado.getMontoPago().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El monto de pago debe ser mayor a 0");
        }
    }

    @Override
    public long calcularHorasContrato(Long empleadoId, LocalDate inicio, LocalDate fin) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        
        long horasPorDia = empleado.horasContratoPorDia();
        long horasTotales = 0;
        LocalDate fecha = inicio;
        while (!fecha.isAfter(fin)) {
            if (empleado.esDiaLaborable(fecha.getDayOfWeek())) {
                horasTotales += horasPorDia;
            }
            fecha = fecha.plusDays(1);
        }
        return horasTotales;
    }

    @Override
    public long calcularHorasReales(Long empleadoId, LocalDate inicio, LocalDate fin) {
        List<RegistroAsistencia> asistencias = registroAsistenciaRepository
                .findByEmpleadoIdAndFechaHoraBetween(empleadoId, inicio.atStartOfDay(), fin.plusDays(1).atStartOfDay());
        
        long horasReales = 0;
        var asistenciasPorDia = asistencias.stream()
                .collect(Collectors.groupingBy(a -> a.getFechaHora().toLocalDate()));
        
        for (var entry : asistenciasPorDia.entrySet()) {
            var registros = entry.getValue();
            var entrada = registros.stream()
                    .filter(r -> "ENTRADA".equals(r.getTipoMarcacion()))
                    .filter(r -> !"RECHAZADO".equals(r.getEstado()))
                    .min((a, b) -> a.getFechaHora().compareTo(b.getFechaHora()));
            var salida = registros.stream()
                    .filter(r -> "SALIDA".equals(r.getTipoMarcacion()))
                    .filter(r -> !"RECHAZADO".equals(r.getEstado()))
                    .max((a, b) -> a.getFechaHora().compareTo(b.getFechaHora()));
            
            if (entrada.isPresent() && salida.isPresent()) {
                horasReales += ChronoUnit.HOURS.between(entrada.get().getFechaHora(), salida.get().getFechaHora());
            }
        }
        return horasReales;
    }

    @Override
    public EmpleadoResponseDTO obtenerResumenPuntualidad(Long empleadoId) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        EmpleadoResponseDTO dto = EmpleadoMapper.toDTO(empleado);
        enriquecerConMetricas(dto, empleado);
        return dto;
    }

    private void enriquecerConMetricas(EmpleadoResponseDTO dto, Empleado empleado) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        
        long faltasMes = registroAsistenciaRepository.findByEmpleadoIdAndFechaHoraBetween(
                empleado.getId(), inicioMes.atStartOfDay(), hoy.plusDays(1).atStartOfDay())
                .stream()
                .filter(r -> r.getEsFalta() != null && r.getEsFalta())
                .count();
        dto.setTotalFaltasMes((int) faltasMes);
        
        long tardanzasMes = registroAsistenciaRepository.findByEmpleadoIdAndFechaHoraBetween(
                empleado.getId(), inicioMes.atStartOfDay(), hoy.plusDays(1).atStartOfDay())
                .stream()
                .filter(r -> r.getMinutosTardanza() != null && r.getMinutosTardanza() > 0)
                .count();
        dto.setTotalTardanzasMes((int) tardanzasMes);
        
        if (empleado.getMetricasBurnout() != null && !empleado.getMetricasBurnout().isEmpty()) {
            empleado.getMetricasBurnout().stream()
                    .max((a, b) -> a.getFechaEvaluacion().compareTo(b.getFechaEvaluacion()))
                    .ifPresent(m -> {
                        dto.setUltimoNivelRiesgo(m.getNivelRiesgo().name());
                        dto.setIndicePuntualidad(m.getIndicePuntualidad());
                    });
        }
    }
}