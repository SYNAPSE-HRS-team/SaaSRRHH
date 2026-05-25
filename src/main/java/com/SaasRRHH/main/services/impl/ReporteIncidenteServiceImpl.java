package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.model.ReporteIncidente;
import com.SaasRRHH.main.repository.ReporteIncidenteRepository;
import com.SaasRRHH.main.services.ReporteIncidenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReporteIncidenteServiceImpl implements ReporteIncidenteService {

    private final ReporteIncidenteRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<ReporteIncidente> listar() {
        return repository.findAllWithRelaciones();
    }

    @Override
    @Transactional(readOnly = true)

    public ReporteIncidente guardar(ReporteIncidente data) {
        return repository.save(data);
    }

    @Override
    @Transactional(readOnly = true)

    public ReporteIncidente obtenerPorId(Long id) {
        return repository.findByIdWithRelaciones(id).orElse(null);
    }

    @Override
    public ReporteIncidente actualizar(Long id, ReporteIncidente data) {
        ReporteIncidente reporte = repository.findById(id).orElse(null);
        if (reporte == null) {
            return null;
        }

        BeanUtils.copyProperties(data, reporte, "id");
        return repository.save(reporte);
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
