package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.BoletaPago;

import java.util.List;
import java.util.Optional;

public interface BoletaPagoService {

    List<BoletaPago> listar();

    Optional<BoletaPago> buscarPorId(Long id);

    BoletaPago guardar(BoletaPago boleta);

    BoletaPago actualizar(Long id, BoletaPago data);

    void eliminar(Long id);
}