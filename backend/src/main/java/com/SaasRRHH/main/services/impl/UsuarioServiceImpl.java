package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.UsuarioRequestDTO;
import com.SaasRRHH.main.DTO.UsuarioResponseDTO;
import com.SaasRRHH.main.mapper.UsuarioMapper;
import com.SaasRRHH.main.model.Rol;
import com.SaasRRHH.main.model.Usuario;
import com.SaasRRHH.main.repository.EmpleadoRepository;
import com.SaasRRHH.main.repository.RolRepository;
import com.SaasRRHH.main.repository.UsuarioRepository;
import com.SaasRRHH.main.services.UsuarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<com.SaasRRHH.main.model.Empleado> empleados = empleadoRepository.findAll();

        Map<Long, com.SaasRRHH.main.model.Empleado> empleadoMap = empleados.stream()
                .filter(e -> e.getUsuario() != null)
                .collect(Collectors.toMap(e -> e.getUsuario().getId(), e -> e, (a, b) -> a));

        return usuarios.stream()
                .map(u -> {
                    UsuarioResponseDTO dto = UsuarioMapper.toDTO(u);
                    com.SaasRRHH.main.model.Empleado emp = empleadoMap.get(u.getId());
                    if (emp != null) {
                        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
                            dto.setNombre(emp.getNombres());
                        }
                        if (dto.getApellido() == null || dto.getApellido().isBlank()) {
                            dto.setApellido(emp.getApellidos());
                        }
                    }
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        UsuarioResponseDTO dto = UsuarioMapper.toDTO(usuario);
        empleadoRepository.findByUsuarioId(id).ifPresent(emp -> {
            if (dto.getNombre() == null || dto.getNombre().isBlank()) {
                dto.setNombre(emp.getNombres());
            }
            if (dto.getApellido() == null || dto.getApellido().isBlank()) {
                dto.setApellido(emp.getApellidos());
            }
        });
        return dto;
    }

    @Override
    public UsuarioResponseDTO guardar(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Usuario usuario = UsuarioMapper.toEntity(dto, rol);

        String passwordEncriptada = passwordEncoder.encode(dto.getPassword());
        usuario.setPassword(passwordEncriptada);

        return UsuarioMapper.toDTO(usuarioRepository.save(usuario));
    }

    @Override
    public void eliminar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuarioRepository.delete(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        UsuarioResponseDTO dto = UsuarioMapper.toDTO(usuario);
        empleadoRepository.findByUsuarioId(usuario.getId()).ifPresent(emp -> {
            if (dto.getNombre() == null || dto.getNombre().isBlank()) {
                dto.setNombre(emp.getNombres());
            }
            if (dto.getApellido() == null || dto.getApellido().isBlank()) {
                dto.setApellido(emp.getApellidos());
            }
        });
        return dto;
    }

    @Override
    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.getEmail().equals(dto.getEmail()) &&
                usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya está siendo usado por otro usuario");
        }

        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        UsuarioMapper.updateEntity(usuario, dto, rol);

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            String passwordEncriptada = passwordEncoder.encode(dto.getPassword());
            usuario.setPassword(passwordEncriptada);
        }

        Usuario actualizado = usuarioRepository.save(usuario);

        empleadoRepository.findByUsuarioId(usuario.getId()).ifPresent(empleado -> {
            if (dto.getNombre() != null) {
                empleado.setNombres(dto.getNombre());
            }
            if (dto.getApellido() != null) {
                empleado.setApellidos(dto.getApellido());
            }
            empleadoRepository.save(empleado);
            log.info("Empleado sincronizado con Usuario #{}", usuario.getId());
        });

        log.info("✅ Usuario actualizado: {}", actualizado.getEmail());
        return UsuarioMapper.toDTO(actualizado);
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
        List<Usuario> usuarios = usuarioRepository.findByActivoTrue();
        List<com.SaasRRHH.main.model.Empleado> empleados = empleadoRepository.findAll();

        Map<Long, com.SaasRRHH.main.model.Empleado> empleadoMap = empleados.stream()
                .filter(e -> e.getUsuario() != null)
                .collect(Collectors.toMap(e -> e.getUsuario().getId(), e -> e, (a, b) -> a));

        return usuarios.stream()
                .map(u -> {
                    UsuarioResponseDTO dto = UsuarioMapper.toDTO(u);
                    com.SaasRRHH.main.model.Empleado emp = empleadoMap.get(u.getId());
                    if (emp != null) {
                        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
                            dto.setNombre(emp.getNombres());
                        }
                        if (dto.getApellido() == null || dto.getApellido().isBlank()) {
                            dto.setApellido(emp.getApellidos());
                        }
                    }
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> buscarPorRol(String rol) {
        return usuarioRepository.buscarPorRol(rol)
                .stream()
                .map(UsuarioMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> usuariosConAccesoReciente(LocalDateTime fecha) {
        return usuarioRepository.usuariosConAccesoReciente(fecha)
                .stream()
                .map(UsuarioMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> contarUsuariosPorRol() {
        return usuarioRepository.contarUsuariosPorRol();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarUsuariosSinEmpleado() {
        return usuarioRepository.findUsuariosSinEmpleado()
                .stream()
                .map(UsuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponseDTO actualizarPerfil(Long id, Map<String, String> profileData) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        if (profileData.containsKey("nombre")) {
            usuario.setNombre(profileData.get("nombre"));
        }
        if (profileData.containsKey("apellido")) {
            usuario.setApellido(profileData.get("apellido"));
        }
        if (profileData.containsKey("telefono")) {
            usuario.setTelefono(profileData.get("telefono"));
        }

        usuarioRepository.save(usuario);

        empleadoRepository.findByUsuarioId(id).ifPresent(empleado -> {
            if (profileData.containsKey("nombre")) {
                empleado.setNombres(profileData.get("nombre"));
            }
            if (profileData.containsKey("apellido")) {
                empleado.setApellidos(profileData.get("apellido"));
            }
            empleadoRepository.save(empleado);
        });

        return UsuarioMapper.toDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO actualizarUltimoAcceso(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setUltimoAcceso(LocalDateTime.now());
        return UsuarioMapper.toDTO(usuarioRepository.save(usuario));
    }
}