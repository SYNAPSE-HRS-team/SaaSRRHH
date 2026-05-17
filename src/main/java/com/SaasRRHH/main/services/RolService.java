package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.Rol;
import java.util.List;
import java.util.Optional;

public interface RolService {

    List<Rol> listar();

    Optional<Rol> buscarPorId(Long id);

    Rol guardar(Rol rol);

    void eliminar(Long id);

    Optional<Rol> buscarPorNombre(String nombre);
}