package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.ReporteDiarioRequestDTO;
import com.SaasRRHH.main.DTO.ReporteDiarioResponseDTO;
import com.SaasRRHH.main.mapper.ReporteDiarioMapper;
import com.SaasRRHH.main.model.ReporteDiario;
import com.SaasRRHH.main.repository.ReporteDiarioRepository;
import com.SaasRRHH.main.services.ReporteDiarioService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReporteDiarioServiceImpl implements ReporteDiarioService {

    private final ReporteDiarioRepository repository;

    // =========================
    // 📋 CRUD
    // =========================

    @Override
    @Transactional(readOnly = true)
    public List<ReporteDiarioResponseDTO> listar() {
        return repository.findAllWithRelaciones()
                .stream()
                .map(ReporteDiarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteDiarioResponseDTO buscarPorId(Long id) {
        ReporteDiario entity = repository.findByIdWithRelaciones(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));

        return ReporteDiarioMapper.toDTO(entity);
    }

    @Override
    @Transactional
    public ReporteDiarioResponseDTO guardar(ReporteDiarioRequestDTO dto) {

        ReporteDiario entity = ReporteDiarioMapper.toEntity(dto);

        return ReporteDiarioMapper.toDTO(repository.save(entity));
    }

    @Override
    @Transactional
    public ReporteDiarioResponseDTO actualizar(Long id, ReporteDiarioRequestDTO dto) {

        ReporteDiario existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));

        ReporteDiario nuevo = ReporteDiarioMapper.toEntity(dto);

        existente.setTarea(nuevo.getTarea());
        existente.setEmpleado(nuevo.getEmpleado());
        existente.setDescripcionTrabajador(nuevo.getDescripcionTrabajador());
        existente.setObservacionSupervisor(nuevo.getObservacionSupervisor());
        existente.setPorcentajeAvance(nuevo.getPorcentajeAvance());
        existente.setEstado(nuevo.getEstado());

        return ReporteDiarioMapper.toDTO(repository.save(existente));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Reporte no encontrado");
        }
        repository.deleteById(id);
    }

    // =========================
    // 📊 CONSULTAS JPQL
    // =========================

    @Override
    @Transactional(readOnly = true)
    public List<ReporteDiarioResponseDTO> buscarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return repository.findByRangoFechas(inicio, fin)
                .stream()
                .map(ReporteDiarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteDiarioResponseDTO> buscarPorEmpleado(Long empleadoId) {
        return repository.findByEmpleado(empleadoId)
                .stream()
                .map(ReporteDiarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteDiarioResponseDTO> buscarPorTarea(Long tareaId) {
        return repository.findByTarea(tareaId)
                .stream()
                .map(ReporteDiarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteDiarioResponseDTO> reportesBajoAvance() {
        return repository.reportesBajoAvance()
                .stream()
                .map(ReporteDiarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteDiarioResponseDTO> reportesDeHoy() {
        return repository.reportesDeHoy()
                .stream()
                .map(ReporteDiarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteDiarioResponseDTO> listarPorEstado(String estado) {
        return repository.findByEstado(
                        ReporteDiario.EstadoReporte.valueOf(estado)
                )
                .stream()
                .map(ReporteDiarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    // =========================
    // 📈 ANALÍTICA
    // =========================

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> reportesPorEmpleado() {
        return repository.reportesPorEmpleado();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> avancePromedioPorTarea() {
        return repository.avancePromedioPorTarea();
    }
}