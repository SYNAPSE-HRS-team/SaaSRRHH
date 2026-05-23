package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.model.BoletaPago;
import com.SaasRRHH.main.repository.BoletaPagoRepository;
import com.SaasRRHH.main.services.BoletaPagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BoletaPagoServiceImpl implements BoletaPagoService {

    private final BoletaPagoRepository repository;

    @Override
    public List<BoletaPago> listar() {
        return repository.findAll();
    }

    @Override
    public Optional<BoletaPago> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    public BoletaPago guardar(BoletaPago boleta) {
        return repository.save(boleta);
    }

    @Override
    public BoletaPago actualizar(Long id, BoletaPago data) {
        data.setId(id);
        return repository.save(data);
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
