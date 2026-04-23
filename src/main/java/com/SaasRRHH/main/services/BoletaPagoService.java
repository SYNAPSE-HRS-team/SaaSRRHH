package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.BoletaPago;
import com.SaasRRHH.main.repository.BoletaPagoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BoletaPagoService {

    private final BoletaPagoRepository repository;

    public BoletaPagoService(BoletaPagoRepository repository) {
        this.repository = repository;
    }

    public List<BoletaPago> listar() {
        return repository.findAll();
    }

    public Optional<BoletaPago> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public BoletaPago guardar(BoletaPago boleta) {
        return repository.save(boleta);
    }

    public BoletaPago actualizar(Long id, BoletaPago data) {
        data.setId(id);
        return repository.save(data);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}