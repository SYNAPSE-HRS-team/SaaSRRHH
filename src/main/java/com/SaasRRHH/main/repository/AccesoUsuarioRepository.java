package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.AccesoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccesoUsuarioRepository
      extends JpaRepository<AccesoUsuario, Long> {

   List<AccesoUsuario> findByUsuarioId(Long usuarioId);

   @Query("SELECT a FROM AccesoUsuario a JOIN FETCH a.usuario")
   List<AccesoUsuario> findAllWithUsuario();

   @Query("SELECT a FROM AccesoUsuario a JOIN FETCH a.usuario WHERE a.idAcceso = :id")
   Optional<AccesoUsuario> findByIdWithUsuario(@Param("id") Long id);

   @Query("SELECT a FROM AccesoUsuario a JOIN FETCH a.usuario WHERE a.usuario.id = :usuarioId")
   List<AccesoUsuario> findByUsuarioIdWithUsuario(@Param("usuarioId") Long usuarioId);
}