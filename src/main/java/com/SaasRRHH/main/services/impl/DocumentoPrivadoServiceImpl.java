package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.model.DocumentoPrivado;
import com.SaasRRHH.main.repository.DocumentoPrivadoRepository;
import com.SaasRRHH.main.services.DocumentoPrivadoService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DocumentoPrivadoServiceImpl implements DocumentoPrivadoService {

    private final DocumentoPrivadoRepository documentoPrivadoRepository;

    @Override
    public List<DocumentoPrivado> listar() {
        return documentoPrivadoRepository.findAll();
    }

    @Override
    public Optional<DocumentoPrivado> buscarPorId(Long id) {
        return documentoPrivadoRepository.findById(id);
    }

    @Override
    public DocumentoPrivado guardar(DocumentoPrivado documentoPrivado) {

        return documentoPrivadoRepository.save(documentoPrivado);
    }

        @Override
        public DocumentoPrivado actualizar(Long id, DocumentoPrivado documentoPrivado) {

            DocumentoPrivado existente = documentoPrivadoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("DocumentoPrivado no encontrado"));

            actualizarDatos(existente, documentoPrivado);

            return documentoPrivadoRepository.save(existente);
        }

        private void actualizarDatos(
                DocumentoPrivado existente,
                DocumentoPrivado nuevo) {

            existente.setEmpleado(nuevo.getEmpleado());
            existente.setTipo(nuevo.getTipo());
            existente.setArchivoUrl(nuevo.getArchivoUrl());
            existente.setFechaVencimiento(nuevo.getFechaVencimiento());
            existente.setActivo(nuevo.getActivo());
        }

    @Override
    public void eliminar(Long id) {

        documentoPrivadoRepository.deleteById(id);
    }
}