package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.BonoDescuento;

import java.util.List;

public interface BonoDescuentoService {
    BonoDescuento crear(BonoDescuento bono);
    List<BonoDescuento> listarPorEmpleadoYPeriodo(Long empleadoId, Integer mes, Integer anio);
    List<BonoDescuento> listarPorPeriodo(Integer mes, Integer anio);
}
