package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.AreaTrabajo;
import java.util.List;
import java.util.Optional;

public interface AreaTrabajoService {

    List<AreaTrabajo> listar();

    Optional<AreaTrabajo> buscarPorId(Long id);

    AreaTrabajo guardar(AreaTrabajo area);

    void eliminar(Long id);

    Optional<AreaTrabajo> buscarPorNombre(String nombre);

    List<AreaTrabajo> listarActivas();
}