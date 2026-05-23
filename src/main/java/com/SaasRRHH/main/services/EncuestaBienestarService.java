package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.Encuestabienestar;

import java.util.List;

public interface EncuestaBienestarService {

    List<Encuestabienestar> listar();

    Encuestabienestar guardar(Encuestabienestar encuesta);

    Encuestabienestar obtenerPorId(Long id);

    Encuestabienestar actualizar(Long id, Encuestabienestar data);

    void eliminar(Long id);
}