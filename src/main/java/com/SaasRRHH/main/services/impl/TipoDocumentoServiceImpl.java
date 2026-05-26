package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.TipoDocumentoRequestDTO;
import com.SaasRRHH.main.DTO.TipoDocumentoResponseDTO;
import com.SaasRRHH.main.mapper.TipoDocumentoMapper;
import com.SaasRRHH.main.model.TipoDocumento;
import com.SaasRRHH.main.repository.TipoDocumentoRepository;
import com.SaasRRHH.main.services.TipoDocumentoService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class TipoDocumentoServiceImpl
        implements TipoDocumentoService {

    private final TipoDocumentoRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<TipoDocumentoResponseDTO> listar() {

        return repository.findAll()
                .stream()
                .map(TipoDocumentoMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TipoDocumentoResponseDTO buscarPorId(
            Long id) {

        TipoDocumento t = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Tipo de documento no encontrado"));

        return TipoDocumentoMapper.toDTO(t);
    }

    @Override
    public TipoDocumentoResponseDTO guardar(
            TipoDocumentoRequestDTO dto) {

        if (repository.findByNombre(
                dto.getNombre()).isPresent()) {

            throw new RuntimeException(
                    "El tipo de documento ya existe");
        }

        TipoDocumento entity =
                TipoDocumentoMapper.toEntity(dto);

        return TipoDocumentoMapper.toDTO(
                repository.save(entity));
    }

    @Override
    public TipoDocumentoResponseDTO actualizar(
            Long id,
            TipoDocumentoRequestDTO dto) {

        TipoDocumento existente =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Tipo de documento no encontrado"));

        TipoDocumentoMapper.updateEntity(
                existente,
                dto);

        return TipoDocumentoMapper.toDTO(
                repository.save(existente));
    }

    @Override
    public void eliminar(Long id) {

        TipoDocumento tipo =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Tipo de documento no encontrado"));

        repository.delete(tipo);
    }

    // ==================================
    // CONSULTAS
    // ==================================

    @Override
    @Transactional(readOnly = true)
    public List<TipoDocumentoResponseDTO>
    listarObligatorios() {

        return repository.findByObligatorioTrue()
                .stream()
                .map(TipoDocumentoMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoDocumentoResponseDTO>
    listarRenovables() {

        return repository.findByRequiereRenovacionTrue()
                .stream()
                .map(TipoDocumentoMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoDocumentoResponseDTO>
    listarPorVigencia() {

        return repository.listarPorVigencia()
                .stream()
                .map(TipoDocumentoMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarObligatorios() {

        return repository.contarObligatorios();
    }
}