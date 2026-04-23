package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.AccesoUsuario;
import com.SaasRRHH.main.repository.AccesoUsuarioRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccesoUsuarioService {

 private final AccesoUsuarioRepository repository;

 public AccesoUsuarioService(
    AccesoUsuarioRepository repository
 ){
   this.repository=repository;
 }


 public List<AccesoUsuario> listar(){
    return repository.findAll();
 }


 public AccesoUsuario buscarPorId(
      Long id){

   return repository.findById(id)
      .orElseThrow(
        ()-> new RuntimeException(
           "Acceso no encontrado"
        )
      );
 }


 public List<AccesoUsuario>
 buscarPorUsuario(
      Long usuarioId){

   return repository
       .findByUsuarioId(usuarioId);
 }


 public AccesoUsuario guardar(
      AccesoUsuario acceso){

   return repository.save(acceso);
 }


 public AccesoUsuario actualizar(
       Long id,
       AccesoUsuario acceso){

   AccesoUsuario actual=
      buscarPorId(id);

   actual.setFechaLogout(
      acceso.getFechaLogout()
   );

   actual.setUserAgent(
      acceso.getUserAgent()
   );

   actual.setExitoso(
      acceso.getExitoso()
   );

   return repository.save(actual);

 }


 public void eliminar(Long id){
    repository.deleteById(id);
 }

}