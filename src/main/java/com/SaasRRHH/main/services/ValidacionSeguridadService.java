package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.ValidacionSeguridad;
import com.SaasRRHH.main.repository.ValidacionSeguridadRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ValidacionSeguridadService {
    private final ValidacionSeguridadRepository repository;

    public ValidacionSeguridadService(ValidacionSeguridadRepository repository) {
        this.repository = repository;
    }

    public List<ValidacionSeguridad> listar() {
        return repository.findAll();
    }

    public Optional<ValidacionSeguridad> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public ValidacionSeguridad guardar(ValidacionSeguridad validacionSeguridad) {
        return repository.save(validacionSeguridad);
    }

    public Optional<ValidacionSeguridad> actualizar(Long id, ValidacionSeguridad validacionSeguridad) {
        return repository.findById(id).map(existing -> {
            validacionSeguridad.setId(id);
            return repository.save(validacionSeguridad);
        });
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
