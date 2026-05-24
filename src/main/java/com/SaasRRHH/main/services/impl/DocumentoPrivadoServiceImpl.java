package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.DocumentoPrivadoRequestDTO;
import com.SaasRRHH.main.DTO.DocumentoPrivadoResponseDTO;
import com.SaasRRHH.main.mapper.DocumentoPrivadoMapper;
import com.SaasRRHH.main.model.DocumentoPrivado;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.TipoDocumento;
import com.SaasRRHH.main.repository.DocumentoPrivadoRepository;
import com.SaasRRHH.main.repository.EmpleadoRepository;
import com.SaasRRHH.main.repository.TipoDocumentoRepository;
import com.SaasRRHH.main.services.DocumentoPrivadoService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentoPrivadoServiceImpl implements DocumentoPrivadoService {

        private final DocumentoPrivadoRepository repository;
        private final EmpleadoRepository empleadoRepository;
        private final TipoDocumentoRepository tipoDocumentoRepository;

        @Override
        public List<DocumentoPrivadoResponseDTO> listar() {
                return repository.findAll()
                                .stream()
                                .map(DocumentoPrivadoMapper::toDTO)
                                .toList();
        }

        @Override
        public DocumentoPrivadoResponseDTO buscarPorId(Long id) {

                DocumentoPrivado d = repository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

                return DocumentoPrivadoMapper.toDTO(d);
        }

        @Override
        public DocumentoPrivadoResponseDTO guardar(DocumentoPrivadoRequestDTO dto) {

                Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId())
                                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

                TipoDocumento tipo = tipoDocumentoRepository.findById(dto.getTipoId())
                                .orElseThrow(() -> new RuntimeException("Tipo documento no encontrado"));

                DocumentoPrivado entidad = DocumentoPrivadoMapper.toEntity(dto, empleado, tipo);

                return DocumentoPrivadoMapper.toDTO(repository.save(entidad));
        }

        @Override
        public DocumentoPrivadoResponseDTO actualizar(Long id, DocumentoPrivadoRequestDTO dto) {

                DocumentoPrivado existente = repository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

                Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId())
                                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

                TipoDocumento tipo = tipoDocumentoRepository.findById(dto.getTipoId())
                                .orElseThrow(() -> new RuntimeException("Tipo documento no encontrado"));

                existente.setEmpleado(empleado);
                existente.setTipo(tipo);
                existente.setArchivoUrl(dto.getArchivoUrl());
                existente.setFechaVencimiento(dto.getFechaVencimiento());
                existente.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

                return DocumentoPrivadoMapper.toDTO(repository.save(existente));
        }

        @Override
        public void eliminar(Long id) {
                repository.deleteById(id);
        }
}