package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.model.Encuestabienestar;
import com.SaasRRHH.main.repository.EncuestaBienestarRepository;
import com.SaasRRHH.main.services.EncuestaBienestarService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EncuestaBienestarServiceImpl implements EncuestaBienestarService {

    private final EncuestaBienestarRepository repository;

    @Override
    public List<Encuestabienestar> listar() {
        return repository.findAll();
    }

    @Override
    public Encuestabienestar guardar(Encuestabienestar encuesta) {
        return repository.save(encuesta);
    }

    @Override
    public Encuestabienestar obtenerPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Encuestabienestar actualizar(Long id, Encuestabienestar data) {
        Encuestabienestar encuesta = repository.findById(id).orElse(null);
        if (encuesta == null) {
            return null;
        }

        BeanUtils.copyProperties(data, encuesta, "id");
        return repository.save(encuesta);
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
