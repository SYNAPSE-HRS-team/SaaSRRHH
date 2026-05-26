package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.FamiliarRequestDTO;
import com.SaasRRHH.main.DTO.FamiliarResponseDTO;
import com.SaasRRHH.main.mapper.FamiliarMapper;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.Familiar;
import com.SaasRRHH.main.repository.EmpleadoRepository;
import com.SaasRRHH.main.repository.FamiliarRepository;
import com.SaasRRHH.main.services.FamiliarService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FamiliarServiceImpl
        implements FamiliarService {

    private final FamiliarRepository familiarRepository;

    private final EmpleadoRepository
            empleadoRepository;

    private final FamiliarMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<FamiliarResponseDTO>
    listar() {

        return familiarRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FamiliarResponseDTO
    buscarPorId(Long id) {

        Familiar familiar =
                familiarRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Familiar no encontrado"));

        return mapper.toDTO(familiar);
    }

    @Override
    public FamiliarResponseDTO
    guardar(FamiliarRequestDTO dto) {

        Empleado empleado =
                empleadoRepository.findById(
                                dto.getEmpleadoId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Empleado no encontrado"));

        Familiar familiar =
                mapper.toEntity(dto, empleado);

        return mapper.toDTO(
                familiarRepository.save(familiar));
    }

    @Override
    public FamiliarResponseDTO
    actualizar(
            Long id,
            FamiliarRequestDTO dto) {

        Familiar familiar =
                familiarRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Familiar no encontrado"));

        Empleado empleado =
                empleadoRepository.findById(
                                dto.getEmpleadoId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Empleado no encontrado"));

        mapper.updateEntity(
                familiar,
                dto,
                empleado);

        return mapper.toDTO(
                familiarRepository.save(familiar));
    }

    @Override
    public void eliminar(Long id) {

        Familiar familiar =
                familiarRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Familiar no encontrado"));

        familiarRepository.delete(familiar);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FamiliarResponseDTO>
    findByEmpleadoId(Long empleadoId) {

        return familiarRepository
                .findByEmpleadoId(empleadoId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    // ===================================
    // CONSULTAS
    // ===================================

    @Override
    @Transactional(readOnly = true)
    public List<FamiliarResponseDTO>
    listarActivos() {

        return familiarRepository.findByActivoTrue()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FamiliarResponseDTO>
    buscarPorParentesco(
            Familiar.Parentesco parentesco) {

        return familiarRepository
                .buscarPorParentesco(parentesco)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FamiliarResponseDTO>
    familiaresQueEstudian() {

        return familiarRepository
                .familiaresQueEstudian()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]>
    contarPorParentesco() {

        return familiarRepository
                .contarPorParentesco();
    }
}