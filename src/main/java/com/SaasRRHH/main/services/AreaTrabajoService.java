package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.AreaTrabajo;
import com.SaasRRHH.main.repository.AreaTrabajoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
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

    public AreaTrabajo guardar(AreaTrabajo areaTrabajo) {
        return repository.save(areaTrabajo);
    }

    public Optional<AreaTrabajo> actualizar(Long id, AreaTrabajo areaTrabajo) {
        return repository.findById(id).map(existing -> {
            areaTrabajo.setId(id);
            return repository.save(areaTrabajo);
        });
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
