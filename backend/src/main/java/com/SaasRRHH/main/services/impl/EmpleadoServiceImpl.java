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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmpleadoServiceImpl implements EmpleadoService {

        private final EmpleadoRepository repository;
        private final UsuarioRepository usuarioRepository;

        @Override
        @Transactional(readOnly = true)
        public List<EmpleadoResponseDTO> listar() {

                return repository.findAll()
                                .stream()
                                .map(EmpleadoMapper::toDTO)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public EmpleadoResponseDTO buscarPorId(Long id) {

                return repository.findById(id)
                                .map(EmpleadoMapper::toDTO)
                                .orElseThrow(() -> new RuntimeException(
                                                "Empleado no encontrado"));
        }

        @Override
        @Transactional(readOnly = true)
        public EmpleadoResponseDTO buscarPorDni(String dni) {

                return repository.findByDni(dni)
                                .map(EmpleadoMapper::toDTO)
                                .orElseThrow(() -> new RuntimeException(
                                                "Empleado no encontrado"));
        }

        @Override
        @Transactional(readOnly = true)
        public List<EmpleadoResponseDTO> listarActivos() {

                return repository.findByActivoTrue()
                                .stream()
                                .map(EmpleadoMapper::toDTO)
                                .toList();
        }

        // En EmpleadoServiceImpl.java
        @Override
        @Transactional(readOnly = true)
        public EmpleadoResponseDTO buscarPorUsuarioId(Long usuarioId) {
                return repository.findByUsuarioId(usuarioId)
                                .map(EmpleadoMapper::toDTO)
                                .orElseThrow(() -> new RuntimeException(
                                                "Empleado no encontrado para el usuario ID: " + usuarioId));
        }

        @Override
        public EmpleadoResponseDTO guardar(EmpleadoRequestDTO dto) {

                if (repository.findByDni(dto.getDni()).isPresent()) {

                        throw new RuntimeException(
                                        "El DNI ya está registrado");
                }

                Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Usuario no encontrado"));

                Empleado empleado = EmpleadoMapper.toEntity(dto, usuario);

                return EmpleadoMapper.toDTO(
                                repository.save(empleado));
        }

        @Override
        public void eliminar(Long id) {

                Empleado empleado = repository.findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Empleado no encontrado"));

                repository.delete(empleado);
        }

        // ===================================
        // CONSULTAS JPQL
        // ===================================

        @Override
        @Transactional(readOnly = true)
        public List<EmpleadoResponseDTO> buscarPorCargo(
                        String cargo) {

                return repository.buscarPorCargo(cargo)
                                .stream()
                                .map(EmpleadoMapper::toDTO)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<EmpleadoResponseDTO> buscarPorCargoYActivo(
                        String cargo,
                        Boolean activo) {

                return repository.buscarPorCargoYEstado(cargo, activo)
                                .stream()
                                .map(EmpleadoMapper::toDTO)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<EmpleadoResponseDTO> listarActivosConUsuario() {

                return repository.listarActivosConUsuario()
                                .stream()
                                .map(EmpleadoMapper::toDTO)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<EmpleadoResponseDTO> contratosVencidos() {

                return repository.contratosVencidos()
                                .stream()
                                .map(EmpleadoMapper::toDTO)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<EmpleadoResponseDTO> contratosPorVencer(
                        LocalDate fechaLimite) {

                return repository.contratosPorVencer(fechaLimite)
                                .stream()
                                .map(EmpleadoMapper::toDTO)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<Object[]> contarEmpleadosPorCargo() {

                return repository.contarEmpleadosPorCargo();
        }
}
