package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.ValidacionSeguridad;

import java.util.List;
import java.util.Optional;

public interface ValidacionSeguridadService {

    List<ValidacionSeguridad> listar();

    Optional<ValidacionSeguridad> buscarPorId(Long id);

    ValidacionSeguridad guardar(ValidacionSeguridad validacionSeguridad);

    Optional<ValidacionSeguridad> actualizar(Long id, ValidacionSeguridad validacionSeguridad);

    void eliminar(Long id);
}
