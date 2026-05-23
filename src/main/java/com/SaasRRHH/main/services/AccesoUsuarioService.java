package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.AccesoUsuario;

import java.util.List;

public interface AccesoUsuarioService {

   List<AccesoUsuario> listar();

   AccesoUsuario buscarPorId(Long id);

   List<AccesoUsuario> buscarPorUsuario(Long usuarioId);

   AccesoUsuario guardar(AccesoUsuario acceso);

   AccesoUsuario actualizar(Long id, AccesoUsuario acceso);

   void eliminar(Long id);
}