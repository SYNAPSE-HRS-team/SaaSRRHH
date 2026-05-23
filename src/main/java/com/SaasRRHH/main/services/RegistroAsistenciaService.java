package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.RegistroAsistencia;
import com.SaasRRHH.main.repository.RegistroAsistenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RegistroAsistenciaService {

    @Autowired
    private RegistroAsistenciaRepository repository;
    
    @Autowired
    private EmpleadoService empleadoService;

    // Listar todos
    public List<RegistroAsistencia> listar() {
        return repository.findAll();
    }

    // Buscar por ID
    public Optional<RegistroAsistencia> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // Guardar registro de asistencia con validaciones
    @Transactional
    public RegistroAsistencia guardar(RegistroAsistencia registroAsistencia) {
        // Validar que el empleado exista
        if (registroAsistencia.getEmpleado() == null || registroAsistencia.getEmpleado().getId() == null) {
            throw new RuntimeException("El empleado es obligatorio");
        }
        
        Optional<Empleado> empleado = empleadoService.buscarPorId(registroAsistencia.getEmpleado().getId());
        if (empleado.isEmpty()) {
            throw new RuntimeException("Empleado no encontrado con id: " + registroAsistencia.getEmpleado().getId());
        }
        
        // Validar tipo de marcación
        if (!registroAsistencia.getTipoMarcacion().matches("ENTRADA|SALIDA")) {
            throw new RuntimeException("Tipo de marcación inválido. Debe ser ENTRADA o SALIDA");
        }
        
        // Validar estado
        if (!registroAsistencia.getEstado().matches("VALIDADO|OBSERVADO|RECHAZADO")) {
            throw new RuntimeException("Estado inválido. Debe ser VALIDADO, OBSERVADO o RECHAZADO");
        }
        
        // Establecer fecha si no viene
        if (registroAsistencia.getFechaHora() == null) {
            registroAsistencia.setFechaHora(LocalDateTime.now());
        }
        
        // Establecer método por defecto
        if (registroAsistencia.getMetodo() == null) {
            registroAsistencia.setMetodo("QR");
        }
        
        return repository.save(registroAsistencia);
    }

    // Registrar entrada
    @Transactional
    public RegistroAsistencia registrarEntrada(Long empleadoId, String metodo) {
        Empleado empleado = empleadoService.buscarPorId(empleadoId)
            .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        
        RegistroAsistencia registro = new RegistroAsistencia();
        registro.setEmpleado(empleado);
        registro.setTipoMarcacion("ENTRADA");
        registro.setMetodo(metodo != null ? metodo : "QR");
        registro.setEstado("VALIDADO");
        registro.setFechaHora(LocalDateTime.now());
        
        return repository.save(registro);
    }
    
    // Registrar salida
    @Transactional
    public RegistroAsistencia registrarSalida(Long empleadoId, String metodo) {
        Empleado empleado = empleadoService.buscarPorId(empleadoId)
            .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        
        RegistroAsistencia registro = new RegistroAsistencia();
        registro.setEmpleado(empleado);
        registro.setTipoMarcacion("SALIDA");
        registro.setMetodo(metodo != null ? metodo : "QR");
        registro.setEstado("VALIDADO");
        registro.setFechaHora(LocalDateTime.now());
        
        return repository.save(registro);
    }

    // Eliminar registro de asistencia
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Registro de asistencia no encontrado con id: " + id);
        }
        repository.deleteById(id);
    }
    
    // Buscar por empleado
    public List<RegistroAsistencia> buscarPorEmpleado(Long empleadoId) {
        return repository.findByEmpleadoId(empleadoId);
    }
    
    // Buscar por empleado y fecha
    public List<RegistroAsistencia> buscarPorEmpleadoYFecha(Long empleadoId, LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(LocalTime.MAX);
        return repository.findByEmpleadoIdAndFechaHoraBetween(empleadoId, inicio, fin);
    }
    
    // Buscar por estado
    public List<RegistroAsistencia> buscarPorEstado(String estado) {
        return repository.findByEstado(estado);
    }
}