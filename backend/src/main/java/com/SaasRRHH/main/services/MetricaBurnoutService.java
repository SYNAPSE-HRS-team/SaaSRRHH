package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.MetricaBurnout;
import java.util.List;

public interface MetricaBurnoutService {

    List<MetricaBurnout> listar();

    MetricaBurnout guardar(MetricaBurnout metrica);

    MetricaBurnout obtenerPorId(Long id);

    List<MetricaBurnout> buscarPorEmpleado(Long empleadoId);

    MetricaBurnout actualizar(Long id, MetricaBurnout metrica);

    void eliminar(Long id);
}