package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.BoletaPagoRequestDTO;

public interface NominaService {

    /**
     * Calcula una boleta preliminar para un empleado en un mes/año.
     * Retorna un DTO con los valores que pueden luego persistirse en `BoletaPago`.
     */
    BoletaPagoRequestDTO calcularBoleta(Long empleadoId, Integer mes, Integer anio);

}
