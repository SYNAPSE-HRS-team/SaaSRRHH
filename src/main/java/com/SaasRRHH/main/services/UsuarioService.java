package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    List<Usuario> listar();

    Optional<Usuario> buscarPorId(Long id);

    Usuario guardar(Usuario usuario);

    void eliminar(Long id);

    Optional<Usuario> buscarPorEmail(String email);

    Usuario actualizarUltimoAcceso(Long id);

    boolean existsByEmail(String email);
}