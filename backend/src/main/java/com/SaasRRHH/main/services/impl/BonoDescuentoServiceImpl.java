package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.model.BonoDescuento;
import com.SaasRRHH.main.repository.BonoDescuentoRepository;
import com.SaasRRHH.main.services.BonoDescuentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BonoDescuentoServiceImpl implements BonoDescuentoService {

    private final BonoDescuentoRepository bonoRepo;

    @Override
    public BonoDescuento crear(BonoDescuento bono) {
        return bonoRepo.save(bono);
    }

    @Override
    public List<BonoDescuento> listarPorEmpleadoYPeriodo(Long empleadoId, Integer mes, Integer anio) {
        return bonoRepo.findByEmpleadoAndPeriodo(empleadoId, mes, anio);
    }

    @Override
    public List<BonoDescuento> listarPorPeriodo(Integer mes, Integer anio) {
        return bonoRepo.findByPeriodo(mes, anio);
    }
}
