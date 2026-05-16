package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.Planilla;

import java.util.List;
import java.util.Optional;

public interface PlanillaService {
    List<Planilla> listar();

    Optional<Planilla> buscarPorId(Long id);

    Planilla guardar (Planilla planilla);

    Planilla actualizar(Long id, Planilla planilla);

    void eliminar(Long id);

}
