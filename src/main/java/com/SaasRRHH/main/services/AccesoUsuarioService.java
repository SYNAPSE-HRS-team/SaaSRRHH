package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.AccesoUsuarioRequestDTO;
import com.SaasRRHH.main.DTO.AccesoUsuarioResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface AccesoUsuarioService {

   // =========================
   // 📋 CRUD
   // =========================

   List<AccesoUsuarioResponseDTO> listar();

   AccesoUsuarioResponseDTO buscarPorId(Long id);

   List<AccesoUsuarioResponseDTO> buscarPorUsuario(Long usuarioId);

   AccesoUsuarioResponseDTO guardar(AccesoUsuarioRequestDTO dto);

   AccesoUsuarioResponseDTO actualizar(Long id, AccesoUsuarioRequestDTO dto);

   void eliminar(Long id);



   List<AccesoUsuarioResponseDTO> listarOrdenadosPorUsuario(Long usuarioId);

   List<AccesoUsuarioResponseDTO> buscarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);

   List<AccesoUsuarioResponseDTO> listarFallidos();

   List<AccesoUsuarioResponseDTO> listarFallidosConUsuario();

   List<AccesoUsuarioResponseDTO> sesionesActivas();

   List<AccesoUsuarioResponseDTO> ultimoAccesoUsuario(Long usuarioId);


   // =========================
   // 📈 ANALÍTICA
   // =========================

   List<Object[]> usuariosMasActivos();

   List<Object[]> accesosExitososPorUsuario();
}