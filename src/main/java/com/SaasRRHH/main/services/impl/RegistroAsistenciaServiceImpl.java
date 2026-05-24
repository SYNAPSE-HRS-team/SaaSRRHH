package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.DTO.EmpleadoResponseDTO;
import com.SaasRRHH.main.model.RegistroAsistencia;
import com.SaasRRHH.main.repository.RegistroAsistenciaRepository;
import com.SaasRRHH.main.services.EmpleadoService;
import com.SaasRRHH.main.services.RegistroAsistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class RegistroAsistenciaServiceImpl implements RegistroAsistenciaService {

    private final RegistroAsistenciaRepository repository;
    private final EmpleadoService empleadoService;

    @Override
    public List<RegistroAsistencia> listar() {
        return repository.findAll();
    }

    @Override
    public Optional<RegistroAsistencia> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public RegistroAsistencia guardar(RegistroAsistencia registroAsistencia) {
        if (registroAsistencia.getEmpleado() == null || registroAsistencia.getEmpleado().getId() == null) {
            throw new RuntimeException("El empleado es obligatorio");
        }

        EmpleadoResponseDTO empleadoDto = empleadoService.buscarPorId(registroAsistencia.getEmpleado().getId());
        if (empleadoDto == null) {
            throw new RuntimeException("Empleado no encontrado con id: " + registroAsistencia.getEmpleado().getId());
        }
        Empleado empleado = new Empleado();
        empleado.setId(empleadoDto.getId());
        registroAsistencia.setEmpleado(empleado);

        if (!registroAsistencia.getTipoMarcacion().matches("ENTRADA|SALIDA")) {
            throw new RuntimeException("Tipo de marcacion invalido. Debe ser ENTRADA o SALIDA");
        }

        if (!registroAsistencia.getEstado().matches("VALIDADO|OBSERVADO|RECHAZADO")) {
            throw new RuntimeException("Estado invalido. Debe ser VALIDADO, OBSERVADO o RECHAZADO");
        }

        if (registroAsistencia.getFechaHora() == null) {
            registroAsistencia.setFechaHora(LocalDateTime.now());
        }

        if (registroAsistencia.getMetodo() == null) {
            registroAsistencia.setMetodo("QR");
        }

        return repository.save(registroAsistencia);
    }

    @Override
    @Transactional
    public RegistroAsistencia registrarEntrada(Long empleadoId, String metodo) {
        EmpleadoResponseDTO empleadoDto = empleadoService.buscarPorId(empleadoId);
        if (empleadoDto == null) {
            throw new RuntimeException("Empleado no encontrado");
        }
        Empleado empleado = new Empleado();
        empleado.setId(empleadoDto.getId());

        RegistroAsistencia registro = new RegistroAsistencia();
        registro.setEmpleado(empleado);
        registro.setTipoMarcacion("ENTRADA");
        registro.setMetodo(metodo != null ? metodo : "QR");
        registro.setEstado("VALIDADO");
        registro.setFechaHora(LocalDateTime.now());

        return repository.save(registro);
    }

    @Override
    @Transactional
    public RegistroAsistencia registrarSalida(Long empleadoId, String metodo) {
        EmpleadoResponseDTO empleadoDto = empleadoService.buscarPorId(empleadoId);
        if (empleadoDto == null) {
            throw new RuntimeException("Empleado no encontrado");
        }
        Empleado empleado = new Empleado();
        empleado.setId(empleadoDto.getId());

        RegistroAsistencia registro = new RegistroAsistencia();
        registro.setEmpleado(empleado);
        registro.setTipoMarcacion("SALIDA");
        registro.setMetodo(metodo != null ? metodo : "QR");
        registro.setEstado("VALIDADO");
        registro.setFechaHora(LocalDateTime.now());

        return repository.save(registro);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Registro de asistencia no encontrado con id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public List<RegistroAsistencia> buscarPorEmpleado(Long empleadoId) {
        return repository.findByEmpleadoId(empleadoId);
    }

    @Override
    public List<RegistroAsistencia> buscarPorEmpleadoYFecha(Long empleadoId, LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(LocalTime.MAX);
        return repository.findByEmpleadoIdAndFechaHoraBetween(empleadoId, inicio, fin);
    }

    @Override
    public List<RegistroAsistencia> buscarPorEstado(String estado) {
        return repository.findByEstado(estado);
    }
}
