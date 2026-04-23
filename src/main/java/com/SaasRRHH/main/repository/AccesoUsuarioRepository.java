package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.AccesoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccesoUsuarioRepository
extends JpaRepository<AccesoUsuario,Long>{

   List<AccesoUsuario>
   findByUsuarioId(Long usuarioId);

}