package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.model.MetricaBurnout;
import com.SaasRRHH.main.repository.MetricaBurnoutRepository;
import com.SaasRRHH.main.services.MetricaBurnoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MetricaBurnoutServiceImpl implements MetricaBurnoutService {

    private final MetricaBurnoutRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<MetricaBurnout> listar() {
        return repository.findAllWithRelaciones();
    }

    @Override
    @Transactional
    public MetricaBurnout guardar(MetricaBurnout metrica) {
        return repository.save(metrica);
    }

    @Override
    @Transactional(readOnly = true)
    public MetricaBurnout obtenerPorId(Long id) {
        return repository.findByIdWithRelaciones(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetricaBurnout> buscarPorEmpleado(Long empleadoId) {
        return repository.findByEmpleadoId(empleadoId);
    }

    @Override
    @Transactional
    public MetricaBurnout actualizar(Long id, MetricaBurnout metrica) {
        MetricaBurnout actual = obtenerPorId(id);

        actual.setNivelRiesgo(metrica.getNivelRiesgo());
        actual.setHorasExtraAcumuladas(metrica.getHorasExtraAcumuladas());
        actual.setTendenciaTardanza(metrica.getTendenciaTardanza());
        actual.setEmpleado(metrica.getEmpleado());

        return repository.save(actual);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Métrica no encontrada");
        }
        repository.deleteById(id);
    }
}
