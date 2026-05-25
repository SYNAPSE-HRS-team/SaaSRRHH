package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.model.ValidacionSeguridad;
import com.SaasRRHH.main.repository.ValidacionSeguridadRepository;
import com.SaasRRHH.main.services.ValidacionSeguridadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ValidacionSeguridadServiceImpl implements ValidacionSeguridadService {

    private final ValidacionSeguridadRepository repository;

    
    @Override
    @Transactional(readOnly = true)
    public List<ValidacionSeguridad> listar() {
        return repository.findAllWithRelaciones();
    }

    @Override
    public Optional<ValidacionSeguridad> buscarPorId(Long id) {
        return repository.findByIdWithRelaciones(id);
    }

    @Override
    public ValidacionSeguridad guardar(ValidacionSeguridad validacionSeguridad) {
        return repository.save(validacionSeguridad);
    }

    @Override
    public Optional<ValidacionSeguridad> actualizar(Long id, ValidacionSeguridad validacionSeguridad) {
        return repository.findById(id).map(existing -> {
            validacionSeguridad.setId(id);
            return repository.save(validacionSeguridad);
        });
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
