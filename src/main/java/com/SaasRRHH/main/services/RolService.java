package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.Rol;
import com.SaasRRHH.main.repository.RolRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolService {

    private final RolRepository repository;

    public RolService(RolRepository repository) {
        this.repository = repository;
    }

    public List<Rol> listar() {
        return repository.findAll();
    }

    public Optional<Rol> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Rol guardar(Rol rol) {
        if (repository.existsByNombreRol(rol.getNombreRol())) {
            throw new RuntimeException("El rol ya existe");
        }
        return repository.save(rol);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    public Optional<Rol> buscarPorNombre(String nombre) {
        return repository.findByNombreRol(nombre);
    }
}
