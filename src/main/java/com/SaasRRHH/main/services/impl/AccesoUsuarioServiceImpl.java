package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.model.AccesoUsuario;
import com.SaasRRHH.main.repository.AccesoUsuarioRepository;
import com.SaasRRHH.main.services.AccesoUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AccesoUsuarioServiceImpl implements AccesoUsuarioService {

    private final AccesoUsuarioRepository repository;

    @Override
    public List<AccesoUsuario> listar() {
        return repository.findAll();
    }

    @Override
    public AccesoUsuario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Acceso no encontrado"));
    }

    @Override
    public List<AccesoUsuario> buscarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }

    @Override
    public AccesoUsuario guardar(AccesoUsuario acceso) {
        return repository.save(acceso);
    }

    @Override
    public AccesoUsuario actualizar(Long id, AccesoUsuario acceso) {
        AccesoUsuario actual = buscarPorId(id);

        actual.setFechaLogout(acceso.getFechaLogout());
        actual.setUserAgent(acceso.getUserAgent());
        actual.setExitoso(acceso.getExitoso());

        return repository.save(actual);
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
