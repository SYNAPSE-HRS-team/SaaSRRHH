package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.TipoDocumentoRequestDTO;
import com.SaasRRHH.main.DTO.TipoDocumentoResponseDTO;
import com.SaasRRHH.main.mapper.TipoDocumentoMapper;
import com.SaasRRHH.main.model.TipoDocumento;
import com.SaasRRHH.main.repository.TipoDocumentoRepository;
import com.SaasRRHH.main.services.TipoDocumentoService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TipoDocumentoServiceImpl implements TipoDocumentoService {

    private final TipoDocumentoRepository repository;

    @Override
    public List<TipoDocumentoResponseDTO> listar() {

        return repository.findAll()
                .stream()
                .map(TipoDocumentoMapper::toDTO)
                .toList();
    }

    @Override
    public TipoDocumentoResponseDTO buscarPorId(Long id) {

        TipoDocumento t = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("TipoDocumento no encontrado"));

        return TipoDocumentoMapper.toDTO(t);
    }

    @Override
    public TipoDocumentoResponseDTO guardar(TipoDocumentoRequestDTO dto) {

        TipoDocumento entity = TipoDocumentoMapper.toEntity(dto);

        return TipoDocumentoMapper.toDTO(repository.save(entity));
    }

    @Override
    public TipoDocumentoResponseDTO actualizar(Long id, TipoDocumentoRequestDTO dto) {

        TipoDocumento existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("TipoDocumento no encontrado"));

        TipoDocumentoMapper.updateEntity(existente, dto);

        return TipoDocumentoMapper.toDTO(repository.save(existente));
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}