package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.Rol;
import com.SaasRRHH.main.repository.RolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
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

    @Transactional
    public Rol guardar(Rol rol) {
        // Validar nombre único
        if (rol.getIdRol() == null && repository.existsByNombreRol(rol.getNombreRol())) {
            throw new RuntimeException("Ya existe un rol con el nombre: " + rol.getNombreRol());
        }
        
        // Para actualización, verificar que el nombre no pertenezca a otro rol
        if (rol.getIdRol() != null) {
            Optional<Rol> existing = repository.findByNombreRol(rol.getNombreRol());
            if (existing.isPresent() && !existing.get().getIdRol().equals(rol.getIdRol())) {
                throw new RuntimeException("Ya existe un rol con el nombre: " + rol.getNombreRol());
            }
        }
        
        return repository.save(rol);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Rol no encontrado con id: " + id);
        }
        repository.deleteById(id);
    }

    public Optional<Rol> buscarPorNombre(String nombre) {
        return repository.findByNombreRol(nombre);
    }
}