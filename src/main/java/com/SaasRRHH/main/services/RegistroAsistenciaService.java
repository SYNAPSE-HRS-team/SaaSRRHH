package com.SaasRRHH.main.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SaasRRHH.main.model.RegistroAsistencia;
import com.SaasRRHH.main.repository.RegistroAsistenciaRepository;

@Service
public class RegistroAsistenciaService {
    @Autowired
    private RegistroAsistenciaRepository repository;

    // Listar todos
    public List<RegistroAsistencia> listar() {
        return repository.findAll();
    }

    // Buscar por ID
    public Optional<RegistroAsistencia> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // Guardar registro de asistencia
    public RegistroAsistencia guardar(RegistroAsistencia registroAsistencia) {
        return repository.save(registroAsistencia);
    }

    // Eliminar registro de asistencia
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
