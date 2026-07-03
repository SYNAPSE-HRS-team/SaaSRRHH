package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.model.Planilla;
import com.SaasRRHH.main.repository.PlanillaRepository;
import com.SaasRRHH.main.services.PlanillaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@RequiredArgsConstructor
@Service
public class PlanillaServiceImpl implements PlanillaService {

    private final PlanillaRepository planillaRepository;

    @Override
    public List<Planilla> listar() {
        return planillaRepository.findAll() ;
    }

    @Override
    public Optional<Planilla> buscarPorId(Long id) {
        return planillaRepository.findById(id);
    }

    @Override
    public Planilla guardar(Planilla planilla) {
        return  planillaRepository.save(planilla);
    }

    @Override
    public Planilla actualizar(Long id, Planilla planilla) {

        Planilla existente = planillaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Planilla no encontrada"));

        actualizarDatos(existente, planilla);

        return planillaRepository.save(existente);
    }

    private void actualizarDatos(Planilla existente, Planilla nueva) {

        existente.setMes(nueva.getMes());
        existente.setAnio(nueva.getAnio());
        existente.setTotalPagado(nueva.getTotalPagado());
        existente.setEstado(nueva.getEstado());
        existente.setFechaCierre(nueva.getFechaCierre());
    }

    @Override
    public Planilla cerrar(Long id) {
        Planilla existente = planillaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Planilla no encontrada"));
        if (existente.getEstado() == Planilla.EstadoPlanilla.CERRADO) {
            throw new RuntimeException("La planilla ya está cerrada");
        }
        existente.setEstado(Planilla.EstadoPlanilla.CERRADO);
        existente.setFechaCierre(java.time.LocalDateTime.now());
        return planillaRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        planillaRepository.deleteById(id);
    }
}
