package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.MetricaBurnout;
import com.SaasRRHH.main.repository.MetricaBurnoutRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetricaBurnoutService {

    private final MetricaBurnoutRepository repository;

    public MetricaBurnoutService(MetricaBurnoutRepository repository) {
        this.repository = repository;
    }

    public List<MetricaBurnout> listar() {
        return repository.findAll();
    }

    public MetricaBurnout guardar(MetricaBurnout metrica) {
        return repository.save(metrica);
    }

    public MetricaBurnout obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));
    }

    public List<MetricaBurnout> buscarPorEmpleado(Long empleadoId) {
        return repository.findByEmpleadoId(empleadoId);
    }

    public MetricaBurnout actualizar(Long id, MetricaBurnout metrica) {

        MetricaBurnout actual = obtenerPorId(id);

        actual.setNivelRiesgo(metrica.getNivelRiesgo());
        actual.setHorasExtraAcumuladas(metrica.getHorasExtraAcumuladas());
        actual.setTendenciaTardanza(metrica.getTendenciaTardanza());
        actual.setEmpleado(metrica.getEmpleado());

        return repository.save(actual);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}