package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.FeedbackAnonimoRequestDTO;
import com.SaasRRHH.main.DTO.FeedbackAnonimoResponseDTO;
import com.SaasRRHH.main.mapper.FeedbackAnonimoMapper;
import com.SaasRRHH.main.model.FeedbackAnonimo;
import com.SaasRRHH.main.repository.FeedbackAnonimoRepository;
import com.SaasRRHH.main.services.FeedbackAnonimoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class FeedbackAnonimoServiceImpl implements FeedbackAnonimoService {

    private final FeedbackAnonimoRepository repository;

    @Override
    public FeedbackAnonimoResponseDTO enviarFeedback(FeedbackAnonimoRequestDTO request) {
        if (request == null || request.getMensaje() == null || request.getMensaje().isBlank()) {
            throw new IllegalArgumentException("Mensaje es requerido");
        }
        FeedbackAnonimo entidad = FeedbackAnonimoMapper.toEntity(request);
        FeedbackAnonimo guardada = repository.save(entidad);
        return FeedbackAnonimoMapper.toDTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackAnonimoResponseDTO> listar() {
        return repository.findAll().stream().map(FeedbackAnonimoMapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackAnonimoResponseDTO> listarPorCategoria(FeedbackAnonimo.CategoriaFeedback categoria) {
        return repository.findByCategoriaOrderByFechaEnvioDesc(categoria).stream().map(FeedbackAnonimoMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackAnonimoResponseDTO> listarPorEstado(FeedbackAnonimo.EstadoFeedback estado) {
        return repository.findByEstadoOrderByFechaEnvioDesc(estado).stream().map(FeedbackAnonimoMapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackAnonimoResponseDTO> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return repository.findByFechaEnvioBetweenOrderByFechaEnvioDesc(inicio, fin).stream()
                .map(FeedbackAnonimoMapper::toDTO).toList();
    }

    @Override
    public FeedbackAnonimoResponseDTO cambiarEstado(Long id, FeedbackAnonimo.EstadoFeedback estado) {
        FeedbackAnonimo f = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feedback no encontrado"));
        f.setEstado(estado);
        FeedbackAnonimo guardada = repository.save(f);
        return FeedbackAnonimoMapper.toDTO(guardada);
    }

    @Override
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Feedback no encontrado");
        }
        repository.deleteById(id);
    }
}
