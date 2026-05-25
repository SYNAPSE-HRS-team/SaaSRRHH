package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.ValidacionSeguridadRequestDTO;
import com.SaasRRHH.main.DTO.ValidacionSeguridadResponseDTO;
import com.SaasRRHH.main.mapper.ValidacionSeguridadMapper;
import com.SaasRRHH.main.model.ValidacionSeguridad;
import com.SaasRRHH.main.repository.ValidacionSeguridadRepository;
import com.SaasRRHH.main.services.ValidacionSeguridadService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ValidacionSeguridadServiceImpl
        implements ValidacionSeguridadService {

    private final ValidacionSeguridadRepository repository;

    // =====================================
    // CRUD
    // =====================================

    @Override
    @Transactional(readOnly = true)
    public List<ValidacionSeguridadResponseDTO> listar() {

        return repository.findAllWithRelaciones()
                .stream()
                .map(ValidacionSeguridadMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ValidacionSeguridadResponseDTO buscarPorId(Long id) {

        ValidacionSeguridad validacion =
                repository.findByIdWithRelaciones(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Validación de seguridad no encontrada"));

        return ValidacionSeguridadMapper.toDTO(validacion);
    }

    @Override
    public ValidacionSeguridadResponseDTO guardar(
            ValidacionSeguridadRequestDTO dto) {

        if (dto.getAsistenciaId() == null) {

            throw new RuntimeException(
                    "La asistencia es obligatoria");
        }

        ValidacionSeguridad entity =
                ValidacionSeguridadMapper.toEntity(dto);

        return ValidacionSeguridadMapper.toDTO(
                repository.save(entity));
    }

    @Override
    public ValidacionSeguridadResponseDTO actualizar(
            Long id,
            ValidacionSeguridadRequestDTO dto) {

        ValidacionSeguridad existente =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Validación no encontrada"));

        ValidacionSeguridad nuevaEntidad =
                ValidacionSeguridadMapper.toEntity(dto);

        existente.setAsistencia(
                nuevaEntidad.getAsistencia());

        existente.setDispositivo(
                nuevaEntidad.getDispositivo());

        existente.setTotpHash(
                nuevaEntidad.getTotpHash());

        existente.setTotpValido(
                nuevaEntidad.getTotpValido());

        return ValidacionSeguridadMapper.toDTO(
                repository.save(existente));
    }

    @Override
    public void eliminar(Long id) {

        ValidacionSeguridad validacion =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Validación no encontrada"));

        repository.delete(validacion);
    }

    // =====================================
    // CONSULTAS
    // =====================================

    @Override
    @Transactional(readOnly = true)
    public List<ValidacionSeguridadResponseDTO>
    buscarPorTotpValido(Boolean valido) {

        return repository.findByTotpValido(valido)
                .stream()
                .map(ValidacionSeguridadMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ValidacionSeguridadResponseDTO>
    recientes() {

        return repository.recientes()
                .stream()
                .map(ValidacionSeguridadMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ValidacionSeguridadResponseDTO>
    buscarPorEmpleado(Long empleadoId) {

        return repository.buscarPorEmpleado(empleadoId)
                .stream()
                .map(ValidacionSeguridadMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ValidacionSeguridadResponseDTO>
    intentosFallidos() {

        return repository.intentosFallidos()
                .stream()
                .map(ValidacionSeguridadMapper::toDTO)
                .toList();
    }
}