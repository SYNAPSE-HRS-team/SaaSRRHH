package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.DispositivoAutorizadoRequestDTO;
import com.SaasRRHH.main.DTO.DispositivoAutorizadoResponseDTO;
import com.SaasRRHH.main.mapper.DispositivoAutorizadoMapper;
import com.SaasRRHH.main.model.DispositivoAutorizado;
import com.SaasRRHH.main.model.Usuario;
import com.SaasRRHH.main.repository.DispositivoAutorizadoRepository;
import com.SaasRRHH.main.repository.UsuarioRepository;
import com.SaasRRHH.main.services.DispositivoAutorizadoService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DispositivoAutorizadoImpl
        implements DispositivoAutorizadoService {

    private final DispositivoAutorizadoRepository repository;
    private final UsuarioRepository usuarioRepository;

    // =====================================
    // CRUD
    // =====================================

    @Override
    @Transactional(readOnly = true)
    public List<DispositivoAutorizadoResponseDTO>
    listarTodo() {

        return repository.findAllWithUsuario()
                .stream()
                .map(DispositivoAutorizadoMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DispositivoAutorizadoResponseDTO
    buscarPorId(Long id) {

        DispositivoAutorizado dispositivo =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dispositivo no encontrado"));

        return DispositivoAutorizadoMapper.toDTO(dispositivo);
    }

    @Override
    public DispositivoAutorizadoResponseDTO guardar(
            DispositivoAutorizadoRequestDTO dto) {

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuario no encontrado"));

        boolean existe =
                repository.existsByUsuarioIdAndHardwareId(
                        dto.getUsuarioId(),
                        dto.getHardwareId());

        if (existe) {

            throw new RuntimeException(
                    "El dispositivo ya está registrado");
        }

        DispositivoAutorizado entity =
                DispositivoAutorizadoMapper.toEntity(dto);

        entity.setUsuario(usuario);

        return DispositivoAutorizadoMapper.toDTO(
                repository.save(entity));
    }

    @Override
    public DispositivoAutorizadoResponseDTO actualizar(
            Long id,
            DispositivoAutorizadoRequestDTO dto) {

        DispositivoAutorizado existente =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dispositivo no encontrado"));

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuario no encontrado"));

        existente.setUsuario(usuario);
        existente.setHardwareId(dto.getHardwareId());
        existente.setFcmToken(dto.getFcmToken());
        existente.setActivo(
                dto.getActivo() != null
                        ? dto.getActivo()
                        : true);

        return DispositivoAutorizadoMapper.toDTO(
                repository.save(existente));
    }

    @Override
    public void eliminar(Long id) {

        DispositivoAutorizado dispositivo =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dispositivo no encontrado"));

        repository.delete(dispositivo);
    }

    // =====================================
    // CONSULTAS DE SEGURIDAD Y CONTROL
    // =====================================

    @Override
    @Transactional(readOnly = true)
    public List<DispositivoAutorizadoResponseDTO>
    listarActivos() {

        return repository.findByActivoTrue()
                .stream()
                .map(DispositivoAutorizadoMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DispositivoAutorizadoResponseDTO>
    buscarPorUsuario(Long usuarioId) {

        return repository.findByUsuarioId(usuarioId)
                .stream()
                .map(DispositivoAutorizadoMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeHardwareRegistrado(
            Long usuarioId,
            String hardwareId) {

        return repository.existsByUsuarioIdAndHardwareId(
                usuarioId,
                hardwareId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DispositivoAutorizadoResponseDTO>
    dispositivosRecientes() {

        LocalDateTime hace30Dias =
                LocalDateTime.now().minusDays(30);

        return repository
                .buscarDispositivosRecientes(hace30Dias)
                .stream()
                .map(DispositivoAutorizadoMapper::toDTO)
                .toList();
    }
}