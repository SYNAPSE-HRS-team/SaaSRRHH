package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.Empleado;

import java.util.List;
import java.util.Optional;

public interface EmpleadoService {

    List<Empleado> listar();

    Optional<Empleado> buscarPorId(Long id);

    Empleado guardar(Empleado empleado);

    void eliminar(Long id);


}
