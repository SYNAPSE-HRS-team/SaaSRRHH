package com.SaasRRHH.main.services;

import com.SaasRRHH.main.entity.Empleado;
import com.SaasRRHH.main.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoService {

    @Autowired
    private EmpleadoRepository repository;

    // 📌 Listar todos
    public List<Empleado> listar() {
        return repository.findAll();
    }

    // 📌 Buscar por ID
    public Optional<Empleado> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // 📌 Guardar empleado
    public Empleado guardar(Empleado empleado) {
        return repository.save(empleado);
    }

    // 📌 Eliminar empleado
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
