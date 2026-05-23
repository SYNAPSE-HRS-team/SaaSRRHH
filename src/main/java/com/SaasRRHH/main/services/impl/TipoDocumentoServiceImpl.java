package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.model.TipoDocumento;
import com.SaasRRHH.main.repository.TipoDocumentoRepository;
import com.SaasRRHH.main.services.TipoDocumentoService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TipoDocumentoServiceImpl implements TipoDocumentoService {

    private final TipoDocumentoRepository tipoDocumentoRepository;

    @Override
    public List<TipoDocumento> listar() {
        return tipoDocumentoRepository.findAll();
    }

    @Override
    public Optional<TipoDocumento> buscarPorId(Long id) {
        return tipoDocumentoRepository.findById(id);
    }

    @Override
    public TipoDocumento guardar(TipoDocumento tipoDocumento) {

        return tipoDocumentoRepository.save(tipoDocumento);
    }

    @Override
    public TipoDocumento actualizar(Long id, TipoDocumento tipoDocumento) {

        TipoDocumento existente = tipoDocumentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TipoDocumento no encontrado"));

        actualizarDatos(existente, tipoDocumento);

        return tipoDocumentoRepository.save(existente);
    }

    private void actualizarDatos(TipoDocumento existente, TipoDocumento nuevo) {

        existente.setNombre(nuevo.getNombre());
        existente.setObligatorio(nuevo.getObligatorio());
        existente.setDiasVigencia(nuevo.getDiasVigencia());
        existente.setRequiereRenovacion(nuevo.getRequiereRenovacion());
        existente.setDescripcion(nuevo.getDescripcion());
    }

    @Override
    public void eliminar(Long id) {

        tipoDocumentoRepository.deleteById(id);
    }
}