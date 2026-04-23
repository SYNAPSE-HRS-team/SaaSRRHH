package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.Usuario;
import com.SaasRRHH.main.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    public List<Usuario> listar() {
        return repository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Usuario guardar(Usuario usuario) {
        // Validar email único para nuevos usuarios
        if (usuario.getId() == null && repository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con el email: " + usuario.getEmail());
        }
        
        // Si es actualización y cambió el email, verificar que no exista en otro usuario
        if (usuario.getId() != null) {
            Optional<Usuario> existing = repository.findByEmail(usuario.getEmail());
            if (existing.isPresent() && !existing.get().getId().equals(usuario.getId())) {
                throw new RuntimeException("Ya existe un usuario con el email: " + usuario.getEmail());
            }
        }
        
        // Establecer valores por defecto
        if (usuario.getActivo() == null) {
            usuario.setActivo(true);
        }
        if (usuario.getFechaCreacion() == null) {
            usuario.setFechaCreacion(LocalDateTime.now());
        }
        
        return repository.save(usuario);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con id: " + id);
        }
        repository.deleteById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return repository.findByEmail(email);
    }
    
    @Transactional
    public Usuario actualizarUltimoAcceso(Long id) {
        Usuario usuario = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setUltimoAcceso(LocalDateTime.now());
        return repository.save(usuario);
    }
    
    // Método auxiliar para pruebas
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}