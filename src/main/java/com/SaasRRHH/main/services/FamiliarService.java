package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.Familiar;

import java.util.List;
import java.util.Optional;

public interface FamiliarService {

    List<Familiar> listar();

    Optional<Familiar> buscarPorId(Long id);

    Familiar guardar(Familiar familiar);

    Familiar actualizar(Long id, Familiar familiar);

    void eliminar(Long id);
}