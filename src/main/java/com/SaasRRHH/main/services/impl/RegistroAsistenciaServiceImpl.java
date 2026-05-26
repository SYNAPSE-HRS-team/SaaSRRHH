package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.EmpleadoResponseDTO;
import com.SaasRRHH.main.DTO.RegistroAsistenciaRequestDTO;
import com.SaasRRHH.main.DTO.RegistroAsistenciaResponseDTO;
import com.SaasRRHH.main.mapper.RegistroAsistenciaMapper;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.RegistroAsistencia;
import com.SaasRRHH.main.repository.RegistroAsistenciaRepository;
import com.SaasRRHH.main.services.EmpleadoService;
import com.SaasRRHH.main.services.RegistroAsistenciaService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistroAsistenciaServiceImpl
        implements RegistroAsistenciaService {

    private final RegistroAsistenciaRepository repository;
    private final EmpleadoService empleadoService;


    @Override
    public List<RegistroAsistencia> listar() {
        return repository.findAll();
    }

    @Override
    public Optional<RegistroAsistencia> buscarPorId(Long id) {
        return repository.findById(id);
                .stream()
                .map(RegistroAsistenciaMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RegistroAsistenciaResponseDTO buscarPorId(Long id) {

        return repository.findById(id)
                .map(RegistroAsistenciaMapper::toDTO)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Registro de asistencia no encontrado"));
    }

    @Override
    public RegistroAsistenciaResponseDTO guardar(
            RegistroAsistenciaRequestDTO dto) {

        if (dto.getEmpleadoId() == null) {

            throw new RuntimeException(
                    "El empleado es obligatorio");
        }

        EmpleadoResponseDTO empleadoDTO =
                empleadoService.buscarPorId(dto.getEmpleadoId());

        if (empleadoDTO == null) {

            throw new RuntimeException(
                    "Empleado no encontrado");
        }

        if (!dto.getTipoMarcacion()
                .matches("ENTRADA|SALIDA")) {

            throw new RuntimeException(
                    "Tipo de marcación inválido");
        }

        RegistroAsistencia entity =
                RegistroAsistenciaMapper.toEntity(dto);

        if (entity.getFechaHora() == null) {
            entity.setFechaHora(LocalDateTime.now());
        }

        if (entity.getMetodo() == null) {
            entity.setMetodo("QR");
        }

        return RegistroAsistenciaMapper.toDTO(
                repository.save(entity));
    }

    @Override
    public void eliminar(Long id) {

        RegistroAsistencia registro =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Registro no encontrado"));

        repository.delete(registro);
    }

    // ===================================
    // REGISTRO ENTRADA / SALIDA
    // ===================================

        private LocalDateTime inicioDelDia(LocalDate fecha) {

                return fecha.atStartOfDay();
        }

        private LocalDateTime finDelDia(LocalDate fecha) {

                return fecha.plusDays(1).atStartOfDay();
        }

    @Override
    public RegistroAsistenciaResponseDTO registrarEntrada(
            Long empleadoId,
            String metodo) {

        EmpleadoResponseDTO empleadoDTO =
                empleadoService.buscarPorId(empleadoId);

        if (empleadoDTO == null) {

            throw new RuntimeException(
                    "Empleado no encontrado");
        }

        boolean yaMarco =
                repository.yaMarcoHoy(
                        empleadoId,
                        inicioDelDia(LocalDate.now()),
                        finDelDia(LocalDate.now()),
                        "ENTRADA");

        if (yaMarco) {

            throw new RuntimeException(
                    "El empleado ya registró entrada hoy");
        }

        Empleado empleado = new Empleado();
        empleado.setId(empleadoId);

        RegistroAsistencia registro =
                new RegistroAsistencia();

        registro.setEmpleado(empleado);
        registro.setTipoMarcacion("ENTRADA");
        registro.setMetodo(
                metodo != null ? metodo : "QR");
        registro.setEstado("VALIDADO");
        registro.setFechaHora(LocalDateTime.now());

        return RegistroAsistenciaMapper.toDTO(
                repository.save(registro));
    }

    @Override
    public RegistroAsistenciaResponseDTO registrarSalida(
            Long empleadoId,
            String metodo) {

        EmpleadoResponseDTO empleadoDTO =
                empleadoService.buscarPorId(empleadoId);

        if (empleadoDTO == null) {

            throw new RuntimeException(
                    "Empleado no encontrado");
        }

        boolean yaMarco =
                repository.yaMarcoHoy(
                        empleadoId,
                        inicioDelDia(LocalDate.now()),
                        finDelDia(LocalDate.now()),
                        "SALIDA");

        if (yaMarco) {

            throw new RuntimeException(
                    "El empleado ya registró salida hoy");
        }

        Empleado empleado = new Empleado();
        empleado.setId(empleadoId);

        RegistroAsistencia registro =
                new RegistroAsistencia();

        registro.setEmpleado(empleado);
        registro.setTipoMarcacion("SALIDA");
        registro.setMetodo(
                metodo != null ? metodo : "QR");
        registro.setEstado("VALIDADO");
        registro.setFechaHora(LocalDateTime.now());

        return RegistroAsistenciaMapper.toDTO(
                repository.save(registro));
    }

    // ===================================
    // CONSULTAS
    // ===================================

    @Override
    @Transactional(readOnly = true)
    public List<RegistroAsistenciaResponseDTO>
    buscarPorEmpleado(Long empleadoId) {

        return repository.findByEmpleadoId(empleadoId)
                .stream()
                .map(RegistroAsistenciaMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroAsistenciaResponseDTO>
    buscarPorEmpleadoYFecha(
            Long empleadoId,
            LocalDate fecha) {

        LocalDateTime inicio =
                fecha.atStartOfDay();

        LocalDateTime fin =
                fecha.atTime(LocalTime.MAX);

        return repository
                .findByEmpleadoIdAndFechaHoraBetween(
                        empleadoId,
                        inicio,
                        fin)
                .stream()
                .map(RegistroAsistenciaMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroAsistenciaResponseDTO>
    buscarPorEstado(String estado) {

        return repository.findByEstado(estado)
                .stream()
                .map(RegistroAsistenciaMapper::toDTO)
                .toList();
    }

    // ===================================
    // CONSULTAS ANALITICAS
    // ===================================

    @Override
    @Transactional(readOnly = true)
    public List<RegistroAsistenciaResponseDTO>
    asistenciasHoy() {

        LocalDate hoy = LocalDate.now();

        return repository.asistenciasHoy(
                        inicioDelDia(hoy),
                        finDelDia(hoy))
                .stream()
                .map(RegistroAsistenciaMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroAsistenciaResponseDTO>
    incidenciasAsistencia() {

        return repository.incidenciasAsistencia()
                .stream()
                .map(RegistroAsistenciaMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarAsistenciasMensuales(
            Long empleadoId,
            LocalDateTime inicio,
            LocalDateTime fin) {

        return repository.contarAsistenciasMensuales(
                empleadoId,
                inicio,
                fin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> rankingTardanzas() {

        return repository.rankingTardanzas();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean yaMarcoHoy(
            Long empleadoId,
            String tipo) {

        return repository.yaMarcoHoy(
                empleadoId,
                inicioDelDia(LocalDate.now()),
                finDelDia(LocalDate.now()),
                tipo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroAsistenciaResponseDTO>
    listarCompleto() {

        return repository.listarCompleto()
                .stream()
                .map(RegistroAsistenciaMapper::toDTO)
                .toList();
    }
}

