package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.ReporteIncidente;

import java.util.List;

public interface ReporteIncidenteService {

    List<ReporteIncidente> listar();

    ReporteIncidente guardar(ReporteIncidente data);

    ReporteIncidente obtenerPorId(Long id);

    ReporteIncidente actualizar(Long id, ReporteIncidente data);

    void eliminar(Long id);
}