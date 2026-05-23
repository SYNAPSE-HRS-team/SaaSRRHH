package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.model.ReporteDiario;
import com.SaasRRHH.main.repository.ReporteDiarioRepository;
import com.SaasRRHH.main.services.ReporteDiarioService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReporteDiarioServiceImpl implements ReporteDiarioService {

    private final ReporteDiarioRepository reporteDiarioRepository;

    @Override
    public List<ReporteDiario> listar() {
        return reporteDiarioRepository.findAll();
    }

    @Override
    public Optional<ReporteDiario> buscarPorId(Long id) {
        return reporteDiarioRepository.findById(id);
    }

    @Override
    public ReporteDiario guardar(ReporteDiario reporteDiario) {

        return reporteDiarioRepository.save(reporteDiario);
    }

    @Override
    public ReporteDiario actualizar(Long id, ReporteDiario reporteDiario) {

        ReporteDiario existente = reporteDiarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ReporteDiario no encontrado"));

        actualizarDatos(existente, reporteDiario);

        return reporteDiarioRepository.save(existente);
    }

    private void actualizarDatos(
            ReporteDiario existente,
            ReporteDiario nuevo) {

        existente.setTarea(nuevo.getTarea());
        existente.setEmpleado(nuevo.getEmpleado());
        existente.setDescripcionTrabajador(nuevo.getDescripcionTrabajador());
        existente.setObservacionSupervisor(nuevo.getObservacionSupervisor());
        existente.setPorcentajeAvance(nuevo.getPorcentajeAvance());
        existente.setEstado(nuevo.getEstado());
    }

    @Override
    public void eliminar(Long id) {

        reporteDiarioRepository.deleteById(id);
    }
}