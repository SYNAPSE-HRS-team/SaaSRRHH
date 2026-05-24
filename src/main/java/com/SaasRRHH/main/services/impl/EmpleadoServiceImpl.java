package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.EmpleadoRequestDTO;
import com.SaasRRHH.main.DTO.EmpleadoResponseDTO;
import com.SaasRRHH.main.mapper.EmpleadoMapper;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.Usuario;
import com.SaasRRHH.main.repository.EmpleadoRepository;
import com.SaasRRHH.main.repository.UsuarioRepository;
import com.SaasRRHH.main.services.EmpleadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository repository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public List<EmpleadoResponseDTO> listar() {
        return repository.findAll()
                .stream()
                .map(EmpleadoMapper::toDTO)
                .toList();
    }

    @Override
    public EmpleadoResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(EmpleadoMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
    }

    @Override
    public EmpleadoResponseDTO buscarPorDni(String dni) {
        return repository.findByDni(dni)
                .map(EmpleadoMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
    }

    @Override
    public List<EmpleadoResponseDTO> listarActivos() {
        return repository.findByActivoTrue()
                .stream()
                .map(EmpleadoMapper::toDTO)
                .toList();
    }

    @Override
    public EmpleadoResponseDTO buscarPorUsuarioId(Long userId) {
        return repository.findByUsuarioId(userId)
                .map(EmpleadoMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
    }

    @Override
    public EmpleadoResponseDTO guardar(EmpleadoRequestDTO dto) {

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Empleado empleado = EmpleadoMapper.toEntity(dto, usuario);

        return EmpleadoMapper.toDTO(repository.save(empleado));
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }


}
