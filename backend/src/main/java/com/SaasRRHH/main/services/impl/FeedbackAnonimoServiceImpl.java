package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.FeedbackAnonimoRequestDTO;
import com.SaasRRHH.main.DTO.FeedbackAnonimoResponseDTO;
import com.SaasRRHH.main.mapper.FeedbackAnonimoMapper;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.FeedbackAnonimo;
import com.SaasRRHH.main.repository.EmpleadoRepository;
import com.SaasRRHH.main.repository.FeedbackAnonimoRepository;
import com.SaasRRHH.main.services.FeedbackAnonimoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class FeedbackAnonimoServiceImpl implements FeedbackAnonimoService {

    private static final Logger log = LoggerFactory.getLogger(FeedbackAnonimoServiceImpl.class);

    private final FeedbackAnonimoRepository repository;
    private final EmpleadoRepository empleadoRepository;

    @Override
    public FeedbackAnonimoResponseDTO enviarFeedback(FeedbackAnonimoRequestDTO request) {
        if (request == null || request.getMensaje() == null || request.getMensaje().isBlank()) {
            throw new IllegalArgumentException("Mensaje es requerido");
        }
        
        FeedbackAnonimo entidad = FeedbackAnonimoMapper.toEntity(request);
        
        // ✅ NUEVO: Asociar empleado si se proporciona ID
        if (request.getEmpleadoId() != null) {
            Empleado empleado = empleadoRepository.findById(request.getEmpleadoId())
                    .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));
            entidad.setEmpleado(empleado);
            log.info("📝 Feedback enviado por empleado: {} {}", empleado.getNombres(), empleado.getApellidos());
        } else {
            log.info("📝 Feedback anónimo enviado");
        }
        
        // ✅ NUEVO: Manejar anonimato
        if (request.getEsAnonimo() != null) {
            entidad.setEsAnonimo(request.getEsAnonimo());
        }
        
        entidad.setEstado(FeedbackAnonimo.EstadoFeedback.PENDIENTE);
        
        FeedbackAnonimo guardada = repository.save(entidad);
        return FeedbackAnonimoMapper.toDTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackAnonimoResponseDTO> listar() {
        return repository.findAll().stream()
                .map(FeedbackAnonimoMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackAnonimoResponseDTO> listarPorCategoria(FeedbackAnonimo.CategoriaFeedback categoria) {
        return repository.findByCategoriaOrderByFechaEnvioDesc(categoria).stream()
                .map(FeedbackAnonimoMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackAnonimoResponseDTO> listarPorEstado(FeedbackAnonimo.EstadoFeedback estado) {
        return repository.findByEstadoOrderByFechaEnvioDesc(estado).stream()
                .map(FeedbackAnonimoMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackAnonimoResponseDTO> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return repository.findByFechaEnvioBetweenOrderByFechaEnvioDesc(inicio, fin).stream()
                .map(FeedbackAnonimoMapper::toDTO)
                .toList();
    }

    @Override
    public FeedbackAnonimoResponseDTO cambiarEstado(Long id, FeedbackAnonimo.EstadoFeedback estado) {
        FeedbackAnonimo f = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feedback no encontrado"));
        f.setEstado(estado);
        FeedbackAnonimo guardada = repository.save(f);
        log.info("🔄 Estado de feedback #{} cambiado a: {}", id, estado);
        return FeedbackAnonimoMapper.toDTO(guardada);
    }

    @Override
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Feedback no encontrado");
        }
        repository.deleteById(id);
        log.info("🗑️ Feedback #{} eliminado", id);
    }

    // ============================================
    // ✅ NUEVOS MÉTODOS
    // ============================================

    /**
     * ✅ Permite al admin responder un feedback y cambiar su estado
     */
    @Override
    public FeedbackAnonimoResponseDTO responderFeedback(Long id, String respuesta, FeedbackAnonimo.EstadoFeedback estado) {
        if (respuesta == null || respuesta.isBlank()) {
            throw new IllegalArgumentException("La respuesta no puede estar vacía");
        }
        
        FeedbackAnonimo feedback = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feedback no encontrado con ID: " + id));
        
        // Validar que el estado sea uno de los permitidos para respuesta
        if (estado != FeedbackAnonimo.EstadoFeedback.REVISADO && 
            estado != FeedbackAnonimo.EstadoFeedback.NO_PROCEDE && 
            estado != FeedbackAnonimo.EstadoFeedback.ACEPTADO) {
            throw new IllegalArgumentException("Estado inválido para respuesta. Use: REVISADO, NO_PROCEDE o ACEPTADO");
        }
        
        feedback.setRespuesta(respuesta);
        feedback.setEstado(estado);
        feedback.setFechaRespuesta(LocalDateTime.now());
        
        FeedbackAnonimo guardado = repository.save(feedback);
        
        log.info("✅ Feedback #{} respondido por admin. Estado: {}, Respuesta: {}", 
                 id, estado, respuesta.length() > 50 ? respuesta.substring(0, 50) + "..." : respuesta);
        
        return FeedbackAnonimoMapper.toDTO(guardado);
    }

    /**
     * ✅ Listar feedback por empleado específico
     */
    @Override
    @Transactional(readOnly = true)
    public List<FeedbackAnonimoResponseDTO> listarPorEmpleado(Long empleadoId) {
        List<FeedbackAnonimo> feedbacks = repository.findByEmpleadoIdOrderByFechaEnvioDesc(empleadoId);
        return feedbacks.stream()
                .map(FeedbackAnonimoMapper::toDTO)
                .toList();
    }

    /**
     * ✅ Listar feedback del empleado autenticado (para que vea sus propios feedbacks)
     */
    @Override
    @Transactional(readOnly = true)
    public List<FeedbackAnonimoResponseDTO> listarMisFeedbacks(Long empleadoId) {
        List<FeedbackAnonimo> feedbacks = repository.findByEmpleadoIdOrderByFechaEnvioDesc(empleadoId);
        return feedbacks.stream()
                .map(FeedbackAnonimoMapper::toDTO)
                .toList();
    }

    /**
     * ✅ Contar feedback pendientes (para alertas en dashboard)
     */
    @Override
    @Transactional(readOnly = true)
    public long contarPendientes() {
        return repository.countByEstado(FeedbackAnonimo.EstadoFeedback.PENDIENTE);
    }
}