package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.EncuestaBienestarRequestDTO;
import com.SaasRRHH.main.DTO.EncuestaBienestarResponseDTO;
import com.SaasRRHH.main.mapper.EncuestaBienestarMapper;
import com.SaasRRHH.main.model.Encuestabienestar;
import com.SaasRRHH.main.repository.EncuestaBienestarRepository;
import com.SaasRRHH.main.services.EncuestaBienestarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EncuestaBienestarServiceImpl implements EncuestaBienestarService {

    private final EncuestaBienestarRepository repository;

    @Override
    public List<EncuestaBienestarResponseDTO> listar() {
        return repository.findAll().stream()
                .map(EncuestaBienestarMapper::toDTO)
                .toList();
    }

    @Override
    public EncuestaBienestarResponseDTO guardar(EncuestaBienestarRequestDTO encuesta) {
        Encuestabienestar entity = EncuestaBienestarMapper.toEntity(encuesta);
        return EncuestaBienestarMapper.toDTO(repository.save(entity));
    }

    @Override
    public EncuestaBienestarResponseDTO obtenerPorId(Long id) {
        return repository.findById(id)
                .map(EncuestaBienestarMapper::toDTO)
                .orElse(null);
    }

    @Override
    public EncuestaBienestarResponseDTO actualizar(Long id, EncuestaBienestarRequestDTO data) {
        Encuestabienestar actual = repository.findById(id).orElse(null);
        if (actual == null) {
            return null;
        }

        Encuestabienestar nuevo = EncuestaBienestarMapper.toEntity(data);
        actual.setEmpleado(nuevo.getEmpleado());
        actual.setFecha(nuevo.getFecha());
        actual.setCargaLaboral(nuevo.getCargaLaboral());
        actual.setApoyoEquipo(nuevo.getApoyoEquipo());
        actual.setProyeccion(nuevo.getProyeccion());

        return EncuestaBienestarMapper.toDTO(repository.save(actual));
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
