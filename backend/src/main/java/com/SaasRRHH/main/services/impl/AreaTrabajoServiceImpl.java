package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.model.AreaTrabajo;
import com.SaasRRHH.main.repository.AreaTrabajoRepository;
import com.SaasRRHH.main.services.AreaTrabajoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class AreaTrabajoServiceImpl implements AreaTrabajoService {

    private final AreaTrabajoRepository repository;

    @Override
    public List<AreaTrabajo> listar() {
        return repository.findAll();
    }

    @Override
    public Optional<AreaTrabajo> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public AreaTrabajo guardar(AreaTrabajo area) {
        if (area.getId() == null && repository.existsByNombre(area.getNombre())) {
            throw new RuntimeException("Ya existe un area con el nombre: " + area.getNombre());
        }

        if (area.getActivo() == null) {
            area.setActivo(true);
        }

        return repository.save(area);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Area no encontrada con id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public Optional<AreaTrabajo> buscarPorNombre(String nombre) {
        return repository.findByNombre(nombre);
    }

    @Override
    public List<AreaTrabajo> listarActivas() {
        return repository.findByActivoTrue();
    }
}
