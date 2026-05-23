package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.repository.EmpleadoRepository;
import com.SaasRRHH.main.services.EmpleadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository repository;

    @Override
    public List<Empleado> listar() {
        return repository.findAll();
    }

    @Override
    public Optional<Empleado> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    public Empleado guardar(Empleado empleado) {
        return repository.save(empleado);
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }



}
