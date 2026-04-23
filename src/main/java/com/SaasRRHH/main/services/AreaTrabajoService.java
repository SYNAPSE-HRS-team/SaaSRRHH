package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.AreaTrabajo;
import com.SaasRRHH.main.repository.AreaTrabajoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AreaTrabajoService {

    private final AreaTrabajoRepository repository;

    public AreaTrabajoService(AreaTrabajoRepository repository) {
        this.repository = repository;
    }

    public List<AreaTrabajo> listar() {
        return repository.findAll();
    }

    public Optional<AreaTrabajo> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public AreaTrabajo guardar(AreaTrabajo area) {
        if (area.getId() == null && repository.existsByNombre(area.getNombre())) {
            throw new RuntimeException("Ya existe un área con el nombre: " + area.getNombre());
        }
        
        if (area.getActivo() == null) {
            area.setActivo(true);
        }
        
        return repository.save(area);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Área no encontrada con id: " + id);
        }
        repository.deleteById(id);
    }

    public Optional<AreaTrabajo> buscarPorNombre(String nombre) {
        return repository.findByNombre(nombre);
    }
    
    public List<AreaTrabajo> listarActivas() {
        return repository.findByActivoTrue();
    }
}