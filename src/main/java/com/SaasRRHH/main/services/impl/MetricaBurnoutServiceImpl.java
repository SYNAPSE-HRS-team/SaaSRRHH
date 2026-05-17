package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.model.MetricaBurnout;
import com.SaasRRHH.main.repository.MetricaBurnoutRepository;
import com.SaasRRHH.main.services.MetricaBurnoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MetricaBurnoutServiceImpl implements MetricaBurnoutService {

    private final MetricaBurnoutRepository repository;

    @Override
    public List<MetricaBurnout> listar() {
        return repository.findAll();
    }

    @Override
    public MetricaBurnout guardar(MetricaBurnout metrica) {
        return repository.save(metrica);
    }

    @Override
    public MetricaBurnout obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));
    }

    @Override
    public List<MetricaBurnout> buscarPorEmpleado(Long empleadoId) {
        return repository.findByEmpleadoId(empleadoId);
    }

    @Override
    public MetricaBurnout actualizar(Long id, MetricaBurnout metrica) {
        MetricaBurnout actual = obtenerPorId(id);

        actual.setNivelRiesgo(metrica.getNivelRiesgo());
        actual.setHorasExtraAcumuladas(metrica.getHorasExtraAcumuladas());
        actual.setTendenciaTardanza(metrica.getTendenciaTardanza());
        actual.setEmpleado(metrica.getEmpleado());

        return repository.save(actual);
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
