package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.DTO.EmpleadoResponseDTO;
import com.SaasRRHH.main.model.TareaAsignada;
import com.SaasRRHH.main.model.TareaAsignada.EstadoTarea;
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
    public List<TareaAsignada> listar() {
        return repository.findAll();
    }

    @Override
    public Optional<TareaAsignada> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public TareaAsignada guardar(TareaAsignada tarea) {
        if (tarea.getEmpleado() == null || tarea.getEmpleado().getId() == null) {
            throw new RuntimeException("El empleado es obligatorio");
        }
        EmpleadoResponseDTO empleadoDto = empleadoService.buscarPorId(tarea.getEmpleado().getId());
        if (empleadoDto == null) {
            throw new RuntimeException("Empleado no encontrado con id: " + tarea.getEmpleado().getId());
        }
        Empleado empleado = new Empleado();
        empleado.setId(empleadoDto.getId());
        tarea.setEmpleado(empleado);

        if (tarea.getSupervisor() == null || tarea.getSupervisor().getId() == null) {
            throw new RuntimeException("El supervisor es obligatorio");
        }
        EmpleadoResponseDTO supervisorDto = empleadoService.buscarPorId(tarea.getSupervisor().getId());
        if (supervisorDto == null) {
            throw new RuntimeException("Supervisor no encontrado con id: " + tarea.getSupervisor().getId());
        }
        Empleado supervisor = new Empleado();
        supervisor.setId(supervisorDto.getId());
        tarea.setSupervisor(supervisor);

        if (tarea.getArea() == null || tarea.getArea().getId() == null) {
            throw new RuntimeException("El area es obligatoria");
        }
        if (areaService.buscarPorId(tarea.getArea().getId()).isEmpty()) {
            throw new RuntimeException("Area no encontrada con id: " + tarea.getArea().getId());
        }

        if (tarea.getFuncion() == null) {
            throw new RuntimeException("La funcion es obligatoria");
        }

        if (tarea.getFecha() == null) {
            tarea.setFecha(LocalDate.now());
        }

        if (tarea.getEstado() == null) {
            tarea.setEstado(EstadoTarea.PENDIENTE);
        }

        return repository.save(tarea);
    }

    @Override
    @Transactional
    public Optional<TareaAsignada> actualizar(Long id, TareaAsignada tarea) {
        return repository.findById(id).map(existing -> {
            tarea.setId(id);
            return repository.save(tarea);
        });
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
    public List<TareaAsignada> buscarPorEmpleado(Long empleadoId) {
        return repository.findByEmpleadoId(empleadoId);
    }

    @Override
    public List<TareaAsignada> buscarPorSupervisor(Long supervisorId) {
        return repository.findBySupervisorId(supervisorId);
    }

    @Override
    public List<TareaAsignada> buscarPorEstado(EstadoTarea estado) {
        return repository.findByEstado(estado);
    }

    @Override
    public List<TareaAsignada> buscarPorEmpleadoYFecha(Long empleadoId, LocalDate fecha) {
        return repository.findByEmpleadoIdAndFecha(empleadoId, fecha);
    }

    @Override
    @Transactional
    public TareaAsignada cambiarEstado(Long id, EstadoTarea nuevoEstado) {
        TareaAsignada tarea = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
        tarea.setEstado(nuevoEstado);
        return repository.save(tarea);
    }
}
