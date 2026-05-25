package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.ReporteIncidenteRequestDTO;
import com.SaasRRHH.main.DTO.ReporteIncidenteResponseDTO;
import com.SaasRRHH.main.mapper.ReporteIncidenteMapper;
import com.SaasRRHH.main.model.ReporteIncidente;
import com.SaasRRHH.main.repository.ReporteIncidenteRepository;
import com.SaasRRHH.main.services.ReporteIncidenteService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReporteIncidenteServiceImpl implements ReporteIncidenteService {

    private final ReporteIncidenteRepository repository;

    // =========================
    // 📋 CRUD
    // =========================

    @Override
    @Transactional(readOnly = true)
    public List<ReporteIncidenteResponseDTO> listar() {
        return repository.findAllWithRelaciones()
                .stream()
                .map(ReporteIncidenteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReporteIncidenteResponseDTO guardar(ReporteIncidenteRequestDTO dto) {
        ReporteIncidente entity = ReporteIncidenteMapper.toEntity(dto);
        ReporteIncidente saved = repository.save(entity);
        return ReporteIncidenteMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteIncidenteResponseDTO obtenerPorId(Long id) {
        ReporteIncidente entity = repository.findByIdWithRelaciones(id)
                .orElseThrow(() -> new RuntimeException("ReporteIncidente no encontrado"));

        return ReporteIncidenteMapper.toDTO(entity);
    }

    @Override
    @Transactional
    public ReporteIncidenteResponseDTO actualizar(Long id, ReporteIncidenteRequestDTO dto) {

        ReporteIncidente existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ReporteIncidente no encontrado"));

        ReporteIncidente actualizado = ReporteIncidenteMapper.toEntity(dto);


        actualizado.setId(existente.getId());

        ReporteIncidente saved = repository.save(actualizado);

        return ReporteIncidenteMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }



    @Override
    @Transactional(readOnly = true)
    public List<ReporteIncidenteResponseDTO> listarConRelaciones() {
        return repository.findAllWithRelaciones()
                .stream()
                .map(ReporteIncidenteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteIncidenteResponseDTO> listarPorEmpleado(Long empleadoId) {
        return repository.findByEmpleado(empleadoId)
                .stream()
                .map(ReporteIncidenteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteIncidenteResponseDTO> buscarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return repository.findByRangoFechas(inicio, fin)
                .stream()
                .map(ReporteIncidenteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteIncidenteResponseDTO> listarPorNivelRiesgo(String nivelRiesgo) {
        return repository.findByNivelRiesgo(
                ReporteIncidente.NivelRiesgo.valueOf(nivelRiesgo)
        ).stream().map(ReporteIncidenteMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteIncidenteResponseDTO> listarPorEstado(String estado) {
        return repository.findByEstado(
                ReporteIncidente.EstadoIncidente.valueOf(estado)
        ).stream().map(ReporteIncidenteMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteIncidenteResponseDTO> incidentesCriticos() {
        return repository.incidentesCriticos()
                .stream()
                .map(ReporteIncidenteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteIncidenteResponseDTO> incidentesDeHoy() {
        return repository.incidentesDeHoy()
                .stream()
                .map(ReporteIncidenteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteIncidenteResponseDTO> incidentesCriticosConDetalle() {
        return repository.incidentesCriticosConDetalle()
                .stream()
                .map(ReporteIncidenteMapper::toDTO)
                .collect(Collectors.toList());
    }




    @Override
    @Transactional(readOnly = true)
    public List<Object[]> incidentesPorEmpleado() {
        return repository.incidentesPorEmpleado();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> incidentesPorRiesgo() {
        return repository.incidentesPorRiesgo();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> incidentesPorArea() {
        return repository.incidentesPorArea();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> incidentesPorSupervisor() {
        return repository.incidentesPorSupervisor();
    }
}