package com.SaasRRHH.main.services;

import java.util.List;
import com.SaasRRHH.main.model.MetricaBurnout;

public interface MetricaBurnoutService {

    List<MetricaBurnout> listar();

    MetricaBurnout guardar(
            MetricaBurnout metrica
    );

    MetricaBurnout obtenerPorId(
            Long id
    );

    List<MetricaBurnout> buscarPorEmpleado(
            Long empleadoId
    );

    MetricaBurnout actualizar(
            Long id,
            MetricaBurnout metrica
    );

    void eliminar(
            Long id
    );
}