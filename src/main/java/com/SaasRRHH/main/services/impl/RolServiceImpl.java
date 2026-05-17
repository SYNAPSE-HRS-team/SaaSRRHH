package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.model.Rol;
import com.SaasRRHH.main.repository.RolRepository;
import com.SaasRRHH.main.services.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class RolServiceImpl implements RolService {

    private final RolRepository repository;

    @Override
    public List<Rol> listar() {
        return repository.findAll();
    }

    @Override
    public Optional<Rol> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Rol guardar(Rol rol) {
        if (rol.getIdRol() == null && repository.existsByNombreRol(rol.getNombreRol())) {
            throw new RuntimeException("Ya existe un rol con el nombre: " + rol.getNombreRol());
        }

        if (rol.getIdRol() != null) {
            Optional<Rol> existing = repository.findByNombreRol(rol.getNombreRol());
            if (existing.isPresent() && !existing.get().getIdRol().equals(rol.getIdRol())) {
                throw new RuntimeException("Ya existe un rol con el nombre: " + rol.getNombreRol());
            }
        }

        return repository.save(rol);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Rol no encontrado con id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public Optional<Rol> buscarPorNombre(String nombre) {
        return repository.findByNombreRol(nombre);
    }
}
