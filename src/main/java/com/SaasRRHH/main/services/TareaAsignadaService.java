package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.TareaAsignada;
import com.SaasRRHH.main.repository.TareaAsignadaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TareaAsignadaService {
    private final TareaAsignadaRepository repository;

    public TareaAsignadaService(TareaAsignadaRepository repository) {
        this.repository = repository;
    }

    public List<TareaAsignada> listar() {
        return repository.findAll();
    }

    public Optional<TareaAsignada> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public TareaAsignada guardar(TareaAsignada tareaAsignada) {
        return repository.save(tareaAsignada);
    }

    public Optional<TareaAsignada> actualizar(Long id, TareaAsignada tareaAsignada) {
        return repository.findById(id).map(existing -> {
            tareaAsignada.setId(id);
            return repository.save(tareaAsignada);
        });
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
