package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.ReporteIncidente;
import com.SaasRRHH.main.repository.ReporteIncidenteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReporteIncidenteService {

    private final ReporteIncidenteRepository repository;

    public ReporteIncidenteService(ReporteIncidenteRepository repository) {
        this.repository = repository;
    }

    public List<ReporteIncidente> listar() {
        return repository.findAll();
    }

    public ReporteIncidente guardar(ReporteIncidente data) {
        return repository.save(data);
    }

    public ReporteIncidente obtenerPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public ReporteIncidente actualizar(Long id, ReporteIncidente data) {
        ReporteIncidente e = repository.findById(id).orElse(null);
        if (e == null) return null;

        BeanUtils.copyProperties(data, e, "id");
        return repository.save(e);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}