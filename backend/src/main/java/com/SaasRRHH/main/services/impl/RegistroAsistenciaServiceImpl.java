package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.AsistenciaCalendarioAnualDTO;
import com.SaasRRHH.main.DTO.AsistenciaCalendarioDiaDTO;
import com.SaasRRHH.main.DTO.AsistenciaCalendarioMesDTO;
import com.SaasRRHH.main.DTO.AsistenciaQrDTO;
import com.SaasRRHH.main.DTO.EmpleadoResponseDTO;
import com.SaasRRHH.main.DTO.RegistroAsistenciaRequestDTO;
import com.SaasRRHH.main.DTO.RegistroAsistenciaResponseDTO;
import com.SaasRRHH.main.mapper.RegistroAsistenciaMapper;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.RegistroAsistencia;
import com.SaasRRHH.main.model.Usuario;
import com.SaasRRHH.main.repository.EmpleadoRepository;
import com.SaasRRHH.main.repository.RegistroAsistenciaRepository;
import com.SaasRRHH.main.repository.UsuarioRepository;
import com.SaasRRHH.main.services.EmpleadoService;
import com.SaasRRHH.main.services.RegistroAsistenciaService;
import com.SaasRRHH.main.services.TotpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistroAsistenciaServiceImpl implements RegistroAsistenciaService {

        private static final String QR_PREFIX = "SAASRRHH_ATT";

        private final RegistroAsistenciaRepository repository;
        private final EmpleadoService empleadoService;
        private final EmpleadoRepository empleadoRepository;
        private final UsuarioRepository usuarioRepository;
        private final TotpService totpService;

        @Override
        @Transactional(readOnly = true)
        public List<RegistroAsistenciaResponseDTO> listar() {
                return repository.listarCompleto().stream().map(RegistroAsistenciaMapper::toDTO).toList();
        }

        @Override
        @Transactional(readOnly = true)
        public RegistroAsistenciaResponseDTO buscarPorId(Long id) {
                return repository.findById(id).map(RegistroAsistenciaMapper::toDTO)
                                .orElseThrow(() -> new RuntimeException("Registro de asistencia no encontrado"));
        }

        @Override
        public RegistroAsistenciaResponseDTO guardar(RegistroAsistenciaRequestDTO dto) {
                validarDto(dto);
                RegistroAsistencia entity = RegistroAsistenciaMapper.toEntity(dto);
                if (entity.getFechaHora() == null) entity.setFechaHora(LocalDateTime.now());
                if (entity.getMetodo() == null) entity.setMetodo("QR");
                if (entity.getEstado() == null) entity.setEstado("VALIDADO");
                return RegistroAsistenciaMapper.toDTO(repository.save(entity));
        }

        @Override
        public RegistroAsistenciaResponseDTO actualizar(Long id, RegistroAsistenciaRequestDTO dto) {
                RegistroAsistencia registro = repository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));
                validarDto(dto);
                Empleado empleado = new Empleado();
                empleado.setId(dto.getEmpleadoId());
                registro.setEmpleado(empleado);
                registro.setFechaHora(dto.getFechaHora() != null ? dto.getFechaHora() : registro.getFechaHora());
                registro.setTipoMarcacion(dto.getTipoMarcacion());
                registro.setMetodo(dto.getMetodo() != null ? dto.getMetodo() : registro.getMetodo());
                registro.setEstado(dto.getEstado() != null ? dto.getEstado() : registro.getEstado());
                registro.setObservaciones(dto.getObservaciones());
                return RegistroAsistenciaMapper.toDTO(repository.save(registro));
        }

        private void validarDto(RegistroAsistenciaRequestDTO dto) {
                if (dto.getEmpleadoId() == null) throw new RuntimeException("El empleado es obligatorio");
                EmpleadoResponseDTO empleadoDTO = empleadoService.buscarPorId(dto.getEmpleadoId());
                if (empleadoDTO == null) throw new RuntimeException("Empleado no encontrado");
                if (dto.getTipoMarcacion() == null || !dto.getTipoMarcacion().matches("ENTRADA|SALIDA")) {
                        throw new RuntimeException("Tipo de marcacion invalido");
                }
        }

        @Override
        public void eliminar(Long id) {
                RegistroAsistencia registro = repository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));
                repository.delete(registro);
        }

        private LocalDateTime inicioDelDia(LocalDate fecha) {
                return fecha.atStartOfDay();
        }

        private LocalDateTime finDelDia(LocalDate fecha) {
                return fecha.plusDays(1).atStartOfDay();
        }

        @Override
        public RegistroAsistenciaResponseDTO registrarEntrada(Long empleadoId, String metodo) {
                empleadoService.buscarPorId(empleadoId);
                if (repository.yaMarcoHoy(empleadoId, inicioDelDia(LocalDate.now()), finDelDia(LocalDate.now()), "ENTRADA")) {
                        throw new RuntimeException("El empleado ya registro entrada hoy");
                }
                return crearMarcacion(empleadoId, "ENTRADA", metodo != null ? metodo : "QR");
        }

        @Override
        public RegistroAsistenciaResponseDTO registrarSalida(Long empleadoId, String metodo) {
                empleadoService.buscarPorId(empleadoId);
                if (repository.yaMarcoHoy(empleadoId, inicioDelDia(LocalDate.now()), finDelDia(LocalDate.now()), "SALIDA")) {
                        throw new RuntimeException("El empleado ya registro salida hoy");
                }
                return crearMarcacion(empleadoId, "SALIDA", metodo != null ? metodo : "QR");
        }

        @Override
        public RegistroAsistenciaResponseDTO registrarPorQr(String payload) {
                if (payload == null || payload.isBlank()) throw new RuntimeException("QR vacio");
                String[] parts = payload.split("\\|");
                if (parts.length != 4 || !QR_PREFIX.equals(parts[0])) throw new RuntimeException("QR invalido");
                Long empleadoId;
                long window;
                try {
                        empleadoId = Long.valueOf(parts[1]);
                        window = Long.parseLong(parts[2]);
                } catch (NumberFormatException ex) {
                        throw new RuntimeException("QR invalido");
                }
                Empleado empleado = empleadoRepository.findById(empleadoId)
                                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
                if (empleado.getTotpSecret() == null || !totpService.verify(empleado.getTotpSecret(), window, parts[3])) {
                        throw new RuntimeException("QR vencido o invalido");
                }
                boolean yaEntrada = repository.yaMarcoHoy(empleadoId, inicioDelDia(LocalDate.now()), finDelDia(LocalDate.now()), "ENTRADA");
                boolean yaSalida = repository.yaMarcoHoy(empleadoId, inicioDelDia(LocalDate.now()), finDelDia(LocalDate.now()), "SALIDA");
                if (yaEntrada && yaSalida) {
                        throw new RuntimeException("El empleado ya registro entrada y salida hoy");
                }
                String tipo = yaEntrada ? "SALIDA" : "ENTRADA";
                return crearMarcacion(empleadoId, tipo, "QR_TOTP");
        }

        private RegistroAsistenciaResponseDTO crearMarcacion(Long empleadoId, String tipo, String metodo) {
                Empleado empleado = new Empleado();
                empleado.setId(empleadoId);
                RegistroAsistencia registro = new RegistroAsistencia();
                registro.setEmpleado(empleado);
                registro.setTipoMarcacion(tipo);
                registro.setMetodo(metodo);
                registro.setEstado("VALIDADO");
                registro.setFechaHora(LocalDateTime.now());
                return RegistroAsistenciaMapper.toDTO(repository.save(registro));
        }

        @Override
        public AsistenciaQrDTO generarQrEmpleadoActual() {
                Empleado empleado = empleadoActual();
                if (empleado.getTotpSecret() == null || empleado.getTotpSecret().isBlank()) {
                        empleado.setTotpSecret(totpService.generateSecret());
                        empleado = empleadoRepository.save(empleado);
                }
                long window = totpService.currentWindow();
                String code = totpService.code(empleado.getTotpSecret(), window);
                String payload = QR_PREFIX + "|" + empleado.getId() + "|" + window + "|" + code;
                String nombre = empleado.getNombres() + " " + empleado.getApellidos();
                return new AsistenciaQrDTO(payload, empleado.getId(), nombre, totpService.secondsRemaining(), totpService.expiresAtEpoch());
        }

        private Empleado empleadoActual() {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                Usuario usuario = usuarioRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                return empleadoRepository.findByUsuarioId(usuario.getId())
                                .orElseGet(() -> crearEmpleadoParaUsuario(usuario));
        }

        private Empleado crearEmpleadoParaUsuario(Usuario usuario) {
                Empleado empleado = new Empleado();
                empleado.setUsuario(usuario);
                String base = usuario.getEmail().split("@")[0];
                empleado.setNombres(base.substring(0, 1).toUpperCase() + base.substring(1));
                empleado.setApellidos("Demo");
                empleado.setDni(String.format("%08d", usuario.getId() % 100000000));
                empleado.setFechaInicioContrato(LocalDate.now());
                empleado.setCargo("Trabajador");
                empleado.setActivo(true);
                empleado.setTotpSecret(totpService.generateSecret());
                return empleadoRepository.save(empleado);
        }

        @Override
        public AsistenciaCalendarioMesDTO calendarioEmpleadoActual(Integer anio, Integer mes) {
                return calendarioEmpleado(empleadoActual().getId(), anio, mes);
        }

        @Override
        public AsistenciaCalendarioAnualDTO calendarioAnualEmpleadoActual(Integer anio) {
                return calendarioAnualEmpleado(empleadoActual().getId(), anio);
        }

        @Override
        @Transactional(readOnly = true)
        public AsistenciaCalendarioMesDTO calendarioEmpleado(Long empleadoId, Integer anio, Integer mes) {
                empleadoService.buscarPorId(empleadoId);
                YearMonth ym = YearMonth.of(anio, mes);
                LocalDate inicio = ym.atDay(1);
                LocalDate fin = ym.plusMonths(1).atDay(1);
                List<RegistroAsistencia> registros = repository
                                .findByEmpleadoIdAndFechaHoraBetween(empleadoId, inicio.atStartOfDay(), fin.atStartOfDay());
                Map<LocalDate, RegistroAsistencia> entradas = registros.stream()
                                .filter(r -> "ENTRADA".equals(r.getTipoMarcacion()))
                                .sorted(Comparator.comparing(RegistroAsistencia::getFechaHora))
                                .collect(Collectors.toMap(r -> r.getFechaHora().toLocalDate(), Function.identity(), (a, b) -> a));
                Map<LocalDate, RegistroAsistencia> salidas = registros.stream()
                                .filter(r -> "SALIDA".equals(r.getTipoMarcacion()))
                                .sorted(Comparator.comparing(RegistroAsistencia::getFechaHora))
                                .collect(Collectors.toMap(r -> r.getFechaHora().toLocalDate(), Function.identity(), (a, b) -> b));
                List<AsistenciaCalendarioDiaDTO> dias = new ArrayList<>();
                LocalDate hoy = LocalDate.now();
                for (int day = 1; day <= ym.lengthOfMonth(); day++) {
                        LocalDate fecha = ym.atDay(day);
                        RegistroAsistencia entrada = entradas.get(fecha);
                        RegistroAsistencia salida = salidas.get(fecha);
                        String estado = estadoDia(fecha, hoy, entrada != null);
                        dias.add(new AsistenciaCalendarioDiaDTO(
                                        fecha.toString(),
                                        estado,
                                        entrada != null ? entrada.getId() : null,
                                        entrada != null ? entrada.getFechaHora().toLocalTime().toString() : null,
                                        salida != null ? salida.getFechaHora().toLocalTime().toString() : null));
                }
                return new AsistenciaCalendarioMesDTO(anio, mes, dias);
        }

        private String estadoDia(LocalDate fecha, LocalDate hoy, boolean asistio) {
                if (asistio) return "ASISTIO";
                boolean laborable = fecha.getDayOfWeek() != DayOfWeek.SATURDAY && fecha.getDayOfWeek() != DayOfWeek.SUNDAY;
                if (laborable && fecha.isBefore(hoy)) return "FALTA";
                return "NEUTRO";
        }

        @Override
        @Transactional(readOnly = true)
        public AsistenciaCalendarioAnualDTO calendarioAnualEmpleado(Long empleadoId, Integer anio) {
                List<AsistenciaCalendarioMesDTO> meses = new ArrayList<>();
                for (int mes = 1; mes <= 12; mes++) meses.add(calendarioEmpleado(empleadoId, anio, mes));
                return new AsistenciaCalendarioAnualDTO(anio, meses);
        }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroAsistenciaResponseDTO> historialEmpleadoActual() {
        Empleado empleado = empleadoActual();
        return repository.findByEmpleadoId(empleado.getId()).stream()
                .sorted(Comparator.comparing(RegistroAsistencia::getFechaHora).reversed())
                .map(RegistroAsistenciaMapper::toDTO)
                .toList();
    }

        @Override
        @Transactional(readOnly = true)
        public List<RegistroAsistenciaResponseDTO> buscarPorEmpleado(Long empleadoId) {
                return repository.findByEmpleadoId(empleadoId).stream().map(RegistroAsistenciaMapper::toDTO).toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<RegistroAsistenciaResponseDTO> buscarPorEmpleadoYFecha(Long empleadoId, LocalDate fecha) {
                return repository.findByEmpleadoIdAndFechaHoraBetween(empleadoId, fecha.atStartOfDay(), fecha.atTime(LocalTime.MAX))
                                .stream().map(RegistroAsistenciaMapper::toDTO).toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<RegistroAsistenciaResponseDTO> buscarPorEstado(String estado) {
                return repository.findByEstado(estado).stream().map(RegistroAsistenciaMapper::toDTO).toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<RegistroAsistenciaResponseDTO> asistenciasPorFecha(LocalDate fecha) {
                return repository.asistenciasHoy(inicioDelDia(fecha), finDelDia(fecha)).stream().map(RegistroAsistenciaMapper::toDTO).toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<RegistroAsistenciaResponseDTO> asistenciasHoy() {
                LocalDate hoy = LocalDate.now();
                return repository.asistenciasHoy(inicioDelDia(hoy), finDelDia(hoy)).stream().map(RegistroAsistenciaMapper::toDTO).toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<RegistroAsistenciaResponseDTO> incidenciasAsistencia() {
                return repository.incidenciasAsistencia().stream().map(RegistroAsistenciaMapper::toDTO).toList();
        }

        @Override
        @Transactional(readOnly = true)
        public Long contarAsistenciasMensuales(Long empleadoId, LocalDateTime inicio, LocalDateTime fin) {
                return repository.contarAsistenciasMensuales(empleadoId, inicio, fin);
        }

        @Override
        @Transactional(readOnly = true)
        public List<Object[]> rankingTardanzas() {
                return repository.rankingTardanzas();
        }

        @Override
        @Transactional(readOnly = true)
        public boolean yaMarcoHoy(Long empleadoId, String tipo) {
                return repository.yaMarcoHoy(empleadoId, inicioDelDia(LocalDate.now()), finDelDia(LocalDate.now()), tipo);
        }

        @Override
        @Transactional(readOnly = true)
        public List<RegistroAsistenciaResponseDTO> listarCompleto() {
                return repository.listarCompleto().stream().map(RegistroAsistenciaMapper::toDTO).toList();
        }
}
