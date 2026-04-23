package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.Encuestabienestar;
import com.SaasRRHH.main.repository.EncuestaBienestarRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EncuestaBienestarService {

    private final EncuestaBienestarRepository repository;

    public EncuestaBienestarService(EncuestaBienestarRepository repository) {
        this.repository = repository;
    }

    public List<Encuestabienestar> listar() {
        return repository.findAll();
    }

    public Encuestabienestar guardar(Encuestabienestar encuesta) {
        return repository.save(encuesta);
    }

    public Encuestabienestar obtenerPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Encuestabienestar actualizar(Long id, Encuestabienestar data) {
        Encuestabienestar e = repository.findById(id).orElse(null);
        if (e == null) return null;

        BeanUtils.copyProperties(data, e, "id");
        return repository.save(e);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}