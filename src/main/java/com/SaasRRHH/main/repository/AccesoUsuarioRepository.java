package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.AccesoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccesoUsuarioRepository
extends JpaRepository<AccesoUsuario,Long>{

   List<AccesoUsuario>
   findByUsuarioId(Long usuarioId);

}