package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.DTO.EmpleadoResponseDTO;
import com.SaasRRHH.main.model.TareaAsignada;
import com.SaasRRHH.main.model.TareaAsignada.EstadoTarea;
import com.SaasRRHH.main.DTO.TareaAsignadaRequestDTO;
import com.SaasRRHH.main.DTO.TareaAsignadaResponseDTO;
import com.SaasRRHH.main.mapper.TareaAsignadaMapper;
import com.SaasRRHH.main.repository.TareaAsignadaRepository;
import com.SaasRRHH.main.services.AreaTrabajoService;
import com.SaasRRHH.main.services.EmpleadoService;
import com.SaasRRHH.main.services.TareaAsignadaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class TareaAsignadaServiceImpl implements TareaAsignadaService {

    private final TareaAsignadaRepository repository;
    private final EmpleadoService empleadoService;
    private final AreaTrabajoService areaService;

    @Override
    public List<TareaAsignadaResponseDTO> listar() {
        return repository.findAll()
                .stream()
                .map(TareaAsignadaMapper::toDTO)
                .toList();
    }

    @Override
    public TareaAsignadaResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(TareaAsignadaMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
    }

    @Override
    @Transactional
    public TareaAsignadaResponseDTO guardar(TareaAsignadaRequestDTO tareaDto) {
        if (tareaDto.getEmpleadoId() == null) {
            throw new RuntimeException("El empleado es obligatorio");
        }
        EmpleadoResponseDTO empleadoDto = empleadoService.buscarPorId(tareaDto.getEmpleadoId());
        if (empleadoDto == null) {
            throw new RuntimeException("Empleado no encontrado con id: " + tareaDto.getEmpleadoId());
        }

        if (tareaDto.getSupervisorId() == null) {
            throw new RuntimeException("El supervisor es obligatorio");
        }
        EmpleadoResponseDTO supervisorDto = empleadoService.buscarPorId(tareaDto.getSupervisorId());
        if (supervisorDto == null) {
            throw new RuntimeException("Supervisor no encontrado con id: " + tareaDto.getSupervisorId());
        }

        if (tareaDto.getAreaId() == null) {
            throw new RuntimeException("El area es obligatoria");
        }
        if (areaService.buscarPorId(tareaDto.getAreaId()).isEmpty()) {
            throw new RuntimeException("Area no encontrada con id: " + tareaDto.getAreaId());
        }

        if (tareaDto.getFuncion() == null) {
            throw new RuntimeException("La funcion es obligatoria");
        }

        TareaAsignada entity = TareaAsignadaMapper.toEntity(tareaDto);
        if (entity.getFecha() == null) entity.setFecha(LocalDate.now());
        if (entity.getEstado() == null) entity.setEstado(EstadoTarea.PENDIENTE);

        TareaAsignada saved = repository.save(entity);
        return TareaAsignadaMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public TareaAsignadaResponseDTO actualizar(Long id, TareaAsignadaRequestDTO tareaDto) {
        return repository.findById(id).map(existing -> {
            tareaDto.setId(id);
            TareaAsignada entity = TareaAsignadaMapper.toEntity(tareaDto);
            entity.setId(id);
            TareaAsignada saved = repository.save(entity);
            return TareaAsignadaMapper.toDTO(saved);
        }).orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Tarea no encontrada con id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public List<TareaAsignadaResponseDTO> buscarPorEmpleado(Long empleadoId) {
        return repository.findByEmpleadoId(empleadoId)
                .stream()
                .map(TareaAsignadaMapper::toDTO)
                .toList();
    }

    @Override
    public List<TareaAsignadaResponseDTO> buscarPorSupervisor(Long supervisorId) {
        return repository.findBySupervisorId(supervisorId)
                .stream()
                .map(TareaAsignadaMapper::toDTO)
                .toList();
    }

    @Override
    public List<TareaAsignadaResponseDTO> buscarPorEstado(EstadoTarea estado) {
        return repository.findByEstado(estado)
                .stream()
                .map(TareaAsignadaMapper::toDTO)
                .toList();
    }

    @Override
    public List<TareaAsignadaResponseDTO> buscarPorEmpleadoYFecha(Long empleadoId, LocalDate fecha) {
        return repository.findByEmpleadoIdAndFecha(empleadoId, fecha)
                .stream()
                .map(TareaAsignadaMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public TareaAsignadaResponseDTO cambiarEstado(Long id, EstadoTarea nuevoEstado) {
        TareaAsignada tarea = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
        tarea.setEstado(nuevoEstado);
        TareaAsignada saved = repository.save(tarea);
        return TareaAsignadaMapper.toDTO(saved);
    }
}
