package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.DTO.EmpleadoResponseDTO;
import com.SaasRRHH.main.model.RegistroAsistencia;
import com.SaasRRHH.main.DTO.RegistroAsistenciaRequestDTO;
import com.SaasRRHH.main.DTO.RegistroAsistenciaResponseDTO;
import com.SaasRRHH.main.mapper.RegistroAsistenciaMapper;
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
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class RegistroAsistenciaServiceImpl implements RegistroAsistenciaService {

    private final RegistroAsistenciaRepository repository;
    private final EmpleadoService empleadoService;

    @Override
    public List<RegistroAsistenciaResponseDTO> listar() {
        return repository.findAll()
                .stream()
                .map(RegistroAsistenciaMapper::toDTO)
                .toList();
    }

    @Override
    public RegistroAsistenciaResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(RegistroAsistenciaMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Registro de asistencia no encontrado"));
    }

    @Override
    @Transactional
    public RegistroAsistenciaResponseDTO guardar(RegistroAsistenciaRequestDTO dto) {
        if (dto.getEmpleadoId() == null) {
            throw new RuntimeException("El empleado es obligatorio");
        }

        EmpleadoResponseDTO empleadoDto = empleadoService.buscarPorId(dto.getEmpleadoId());
        if (empleadoDto == null) {
            throw new RuntimeException("Empleado no encontrado con id: " + dto.getEmpleadoId());
        }

        if (dto.getTipoMarcacion() == null || !dto.getTipoMarcacion().matches("ENTRADA|SALIDA")) {
            throw new RuntimeException("Tipo de marcacion invalido. Debe ser ENTRADA o SALIDA");
        }

        if (dto.getEstado() == null || !dto.getEstado().matches("VALIDADO|OBSERVADO|RECHAZADO")) {
            throw new RuntimeException("Estado invalido. Debe ser VALIDADO, OBSERVADO o RECHAZADO");
        }

        RegistroAsistencia entity = RegistroAsistenciaMapper.toEntity(dto);
        if (entity.getFechaHora() == null) entity.setFechaHora(LocalDateTime.now());
        if (entity.getMetodo() == null) entity.setMetodo("QR");

        RegistroAsistencia saved = repository.save(entity);
        return RegistroAsistenciaMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public RegistroAsistenciaResponseDTO registrarEntrada(Long empleadoId, String metodo) {
        EmpleadoResponseDTO empleadoDto = empleadoService.buscarPorId(empleadoId);
        if (empleadoDto == null) {
            throw new RuntimeException("Empleado no encontrado");
        }
        Empleado empleado = new Empleado();
        empleado.setId(empleadoDto.getId());

        RegistroAsistencia registro = new RegistroAsistencia();
        registro.setEmpleado(empleado);
        registro.setTipoMarcacion("ENTRADA");
        registro.setMetodo(metodo != null ? metodo : "QR");
        registro.setEstado("VALIDADO");
        registro.setFechaHora(LocalDateTime.now());

        return RegistroAsistenciaMapper.toDTO(repository.save(registro));
    }

    @Override
    @Transactional
    public RegistroAsistenciaResponseDTO registrarSalida(Long empleadoId, String metodo) {
        EmpleadoResponseDTO empleadoDto = empleadoService.buscarPorId(empleadoId);
        if (empleadoDto == null) {
            throw new RuntimeException("Empleado no encontrado");
        }
        Empleado empleado = new Empleado();
        empleado.setId(empleadoDto.getId());

        RegistroAsistencia registro = new RegistroAsistencia();
        registro.setEmpleado(empleado);
        registro.setTipoMarcacion("SALIDA");
        registro.setMetodo(metodo != null ? metodo : "QR");
        registro.setEstado("VALIDADO");
        registro.setFechaHora(LocalDateTime.now());

        return RegistroAsistenciaMapper.toDTO(repository.save(registro));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Registro de asistencia no encontrado con id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public List<RegistroAsistenciaResponseDTO> buscarPorEmpleado(Long empleadoId) {
        return repository.findByEmpleadoId(empleadoId)
                .stream()
                .map(RegistroAsistenciaMapper::toDTO)
                .toList();
    }

    @Override
    public List<RegistroAsistenciaResponseDTO> buscarPorEmpleadoYFecha(Long empleadoId, LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(LocalTime.MAX);
        return repository.findByEmpleadoIdAndFechaHoraBetween(empleadoId, inicio, fin)
                .stream()
                .map(RegistroAsistenciaMapper::toDTO)
                .toList();
    }

    @Override
    public List<RegistroAsistenciaResponseDTO> buscarPorEstado(String estado) {
        return repository.findByEstado(estado)
                .stream()
                .map(RegistroAsistenciaMapper::toDTO)
                .toList();
    }
}
