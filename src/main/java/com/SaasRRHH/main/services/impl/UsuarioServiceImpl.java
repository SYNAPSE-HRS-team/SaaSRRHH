package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.UsuarioRequestDTO;
import com.SaasRRHH.main.DTO.UsuarioResponseDTO;
import com.SaasRRHH.main.mapper.UsuarioMapper;
import com.SaasRRHH.main.model.Rol;
import com.SaasRRHH.main.model.Usuario;
import com.SaasRRHH.main.repository.RolRepository;
import com.SaasRRHH.main.repository.UsuarioRepository;
import com.SaasRRHH.main.services.UsuarioService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl
        implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar() {

        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {

        return usuarioRepository.findById(id)
                .map(UsuarioMapper::toDTO)
                .orElseThrow(() -> new RuntimeException(
                        "Usuario no encontrado"));
    }

    @Override
    public UsuarioResponseDTO guardar(
            UsuarioRequestDTO dto) {

        if (usuarioRepository.existsByEmail(
                dto.getEmail())) {

            throw new RuntimeException(
                    "El email ya está registrado");
        }

        Rol rol = rolRepository.findById(
                dto.getRolId())
                .orElseThrow(() -> new RuntimeException(
                        "Rol no encontrado"));

        Usuario usuario = UsuarioMapper.toEntity(dto, rol);

        return UsuarioMapper.toDTO(
                usuarioRepository.save(usuario));
    }

    @Override
    public void eliminar(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Usuario no encontrado"));

        usuarioRepository.delete(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorEmail(
            String email) {

        return usuarioRepository.findByEmail(email)
                .map(UsuarioMapper::toDTO)
                .orElseThrow(() -> new RuntimeException(
                        "Usuario no encontrado"));
    }

    @Override
    public UsuarioResponseDTO actualizarUltimoAcceso(
            Long id) {

        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Usuario no encontrado"));

        u.setUltimoAcceso(LocalDateTime.now());

        return UsuarioMapper.toDTO(
                usuarioRepository.save(u));
    }

    @Override
    public boolean existsByEmail(String email) {

        return usuarioRepository.existsByEmail(email);
    }

    // ===================================
    // CONSULTAS JPQL
    // ===================================

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarUsuariosActivos() {

        return usuarioRepository
                .findByActivoTrue()
                .stream()
                .map(UsuarioMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> buscarPorRol(String rol) {

        return usuarioRepository
                .buscarPorRol(rol)
                .stream()
                .map(UsuarioMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> usuariosConAccesoReciente(
            LocalDateTime fecha) {

        return usuarioRepository
                .usuariosConAccesoReciente(fecha)
                .stream()
                .map(UsuarioMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> contarUsuariosPorRol() {

        return usuarioRepository
                .contarUsuariosPorRol();
    }
}