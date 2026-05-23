package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.FamiliarDTO;
import com.SaasRRHH.main.mapper.FamiliarMapper;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.Familiar;
import com.SaasRRHH.main.repository.EmpleadoRepository;
import com.SaasRRHH.main.repository.FamiliarRepository;
import com.SaasRRHH.main.services.FamiliarService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FamiliarServiceImpl implements FamiliarService {

    private final FamiliarRepository familiarRepository;
    private final EmpleadoRepository empleadoRepository;
    private final FamiliarMapper mapper;

    @Override
    public List<FamiliarDTO> listar() {
        return familiarRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public FamiliarDTO buscarPorId(Long id) {
        Familiar familiar = familiarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Familiar no encontrado"));

        return mapper.toDTO(familiar);
    }

    @Override
    public FamiliarDTO guardar(FamiliarDTO dto) {

        Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        Familiar familiar = mapper.toEntity(dto, empleado);

        return mapper.toDTO(familiarRepository.save(familiar));
    }

    @Override
    public FamiliarDTO actualizar(Long id, FamiliarDTO dto) {

        Familiar familiar = familiarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Familiar no encontrado"));

        Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        mapper.updateEntity(familiar, dto, empleado);

        return mapper.toDTO(familiarRepository.save(familiar));
    }

    @Override
    public void eliminar(Long id) {
        if (!familiarRepository.existsById(id)) {
            throw new RuntimeException("Familiar no encontrado");
        }
        familiarRepository.deleteById(id);
    }

    @Override
    public List<FamiliarDTO> findByEmpleadoId(Long empleadoId) {
        return familiarRepository.findByEmpleadoId(empleadoId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }
}