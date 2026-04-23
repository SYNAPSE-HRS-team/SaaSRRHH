package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.TareaAsignada;
import com.SaasRRHH.main.model.TareaAsignada.EstadoTarea;
import com.SaasRRHH.main.repository.TareaAsignadaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TareaAsignadaService {

    private final TareaAsignadaRepository repository;
    private final EmpleadoService empleadoService;
    private final AreaTrabajoService areaService;

    public TareaAsignadaService(TareaAsignadaRepository repository, 
                                EmpleadoService empleadoService,
                                AreaTrabajoService areaService) {
        this.repository = repository;
        this.empleadoService = empleadoService;
        this.areaService = areaService;
    }

    public List<TareaAsignada> listar() {
        return repository.findAll();
    }

    public Optional<TareaAsignada> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public TareaAsignada guardar(TareaAsignada tarea) {
        // Validar empleado
        if (tarea.getEmpleado() == null || tarea.getEmpleado().getId() == null) {
            throw new RuntimeException("El empleado es obligatorio");
        }
        Optional<Empleado> empleado = empleadoService.buscarPorId(tarea.getEmpleado().getId());
        if (empleado.isEmpty()) {
            throw new RuntimeException("Empleado no encontrado con id: " + tarea.getEmpleado().getId());
        }
        tarea.setEmpleado(empleado.get());
        
        // Validar supervisor
        if (tarea.getSupervisor() == null || tarea.getSupervisor().getId() == null) {
            throw new RuntimeException("El supervisor es obligatorio");
        }
        Optional<Empleado> supervisor = empleadoService.buscarPorId(tarea.getSupervisor().getId());
        if (supervisor.isEmpty()) {
            throw new RuntimeException("Supervisor no encontrado con id: " + tarea.getSupervisor().getId());
        }
        tarea.setSupervisor(supervisor.get());
        
        // Validar área
        if (tarea.getArea() == null || tarea.getArea().getId() == null) {
            throw new RuntimeException("El área es obligatoria");
        }
        if (areaService.buscarPorId(tarea.getArea().getId()).isEmpty()) {
            throw new RuntimeException("Área no encontrada con id: " + tarea.getArea().getId());
        }
        
        // Validar función
        if (tarea.getFuncion() == null) {
            throw new RuntimeException("La función es obligatoria");
        }
        
        // Validar fecha
        if (tarea.getFecha() == null) {
            tarea.setFecha(LocalDate.now());
        }
        
        // Establecer estado por defecto
        if (tarea.getEstado() == null) {
            tarea.setEstado(EstadoTarea.PENDIENTE);
        }
        
        return repository.save(tarea);
    }

    @Transactional
    public Optional<TareaAsignada> actualizar(Long id, TareaAsignada tarea) {
        return repository.findById(id).map(existing -> {
            tarea.setId(id);
            return repository.save(tarea);
        });
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Tarea no encontrada con id: " + id);
        }
        repository.deleteById(id);
    }
    
    // Métodos adicionales útiles
    public List<TareaAsignada> buscarPorEmpleado(Long empleadoId) {
        return repository.findByEmpleadoId(empleadoId);
    }
    
    public List<TareaAsignada> buscarPorSupervisor(Long supervisorId) {
        return repository.findBySupervisorId(supervisorId);
    }
    
    public List<TareaAsignada> buscarPorEstado(EstadoTarea estado) {
        return repository.findByEstado(estado);
    }
    
    public List<TareaAsignada> buscarPorEmpleadoYFecha(Long empleadoId, LocalDate fecha) {
        return repository.findByEmpleadoIdAndFecha(empleadoId, fecha);
    }
    
    @Transactional
    public TareaAsignada cambiarEstado(Long id, EstadoTarea nuevoEstado) {
        TareaAsignada tarea = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
        tarea.setEstado(nuevoEstado);
        return repository.save(tarea);
    }
}