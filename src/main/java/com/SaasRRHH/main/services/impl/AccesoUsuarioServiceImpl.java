package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.AccesoUsuarioRequestDTO;
import com.SaasRRHH.main.DTO.AccesoUsuarioResponseDTO;
import com.SaasRRHH.main.mapper.AccesoUsuarioMapper;
import com.SaasRRHH.main.model.AccesoUsuario;
import com.SaasRRHH.main.repository.AccesoUsuarioRepository;
import com.SaasRRHH.main.services.AccesoUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AccesoUsuarioServiceImpl implements AccesoUsuarioService {

    private final AccesoUsuarioRepository repository;

    // =========================
    // 📋 CRUD
    // =========================

    @Override
    @Transactional(readOnly = true)
    public List<AccesoUsuarioResponseDTO> listar() {
        return repository.findAllWithUsuario()
                .stream()
                .map(AccesoUsuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AccesoUsuarioResponseDTO buscarPorId(Long id) {
        AccesoUsuario entity = repository.findByIdWithUsuario(id)
                .orElseThrow(() -> new RuntimeException("Acceso no encontrado"));

        return AccesoUsuarioMapper.toDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccesoUsuarioResponseDTO> buscarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioIdWithUsuario(usuarioId)
                .stream()
                .map(AccesoUsuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AccesoUsuarioResponseDTO guardar(AccesoUsuarioRequestDTO dto) {
        AccesoUsuario entity = AccesoUsuarioMapper.toEntity(dto);
        return AccesoUsuarioMapper.toDTO(repository.save(entity));
    }

    @Override
    @Transactional
    public AccesoUsuarioResponseDTO actualizar(Long id, AccesoUsuarioRequestDTO dto) {

        AccesoUsuario actual = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Acceso no encontrado"));

        actual.setFechaLogout(dto.getFechaLogout());
        actual.setUserAgent(dto.getUserAgent());
        actual.setExitoso(dto.getExitoso());

        if (dto.getUsuarioId() != null) {
            actual.setUsuario(
                    AccesoUsuarioMapper.toEntity(dto).getUsuario()
            );
        }

        return AccesoUsuarioMapper.toDTO(repository.save(actual));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    // =========================
    // 📊 CONSULTAS JPQL
    // =========================

    @Override
    @Transactional(readOnly = true)
    public List<AccesoUsuarioResponseDTO> listarOrdenadosPorUsuario(Long usuarioId) {
        return repository.findAccesosOrdenadosPorUsuario(usuarioId)
                .stream()
                .map(AccesoUsuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccesoUsuarioResponseDTO> buscarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return repository.findByRangoFechas(inicio, fin)
                .stream()
                .map(AccesoUsuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccesoUsuarioResponseDTO> listarFallidos() {
        return repository.findByExitosoFalse()
                .stream()
                .map(AccesoUsuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccesoUsuarioResponseDTO> listarFallidosConUsuario() {
        return repository.findFailedLoginsWithUser()
                .stream()
                .map(AccesoUsuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccesoUsuarioResponseDTO> sesionesActivas() {
        return repository.sesionesActivas()
                .stream()
                .map(AccesoUsuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccesoUsuarioResponseDTO> ultimoAccesoUsuario(Long usuarioId) {
        return repository.ultimoAccesoUsuario(usuarioId)
                .stream()
                .map(AccesoUsuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    // =========================
    // 📈 ANALÍTICA
    // =========================

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> usuariosMasActivos() {
        return repository.usuariosMasActivos();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> accesosExitososPorUsuario() {
        return repository.accesosExitososPorUsuario();
    }
}