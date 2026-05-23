package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.ReporteDiario;

import java.util.List;
import java.util.Optional;

public interface ReporteDiarioService {

    List<ReporteDiario> listar();

    Optional<ReporteDiario> buscarPorId(Long id);

    ReporteDiario guardar(ReporteDiario reporteDiario);

    ReporteDiario actualizar(Long id, ReporteDiario reporteDiario);

    void eliminar(Long id);
}
