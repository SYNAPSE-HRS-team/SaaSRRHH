package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.AccesoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccesoUsuarioRepository extends JpaRepository<AccesoUsuario, Long> {


   @Query("SELECT a FROM AccesoUsuario a JOIN FETCH a.usuario")
   List<AccesoUsuario> findAllWithUsuario();

   @Query("SELECT a FROM AccesoUsuario a JOIN FETCH a.usuario WHERE a.idAcceso = :id")
   Optional<AccesoUsuario> findByIdWithUsuario(@Param("id") Long id);

   @Query("SELECT a FROM AccesoUsuario a JOIN FETCH a.usuario WHERE a.usuario.id = :usuarioId")
   List<AccesoUsuario> findByUsuarioIdWithUsuario(@Param("usuarioId") Long usuarioId);



   @Query("""
        SELECT a FROM AccesoUsuario a
        JOIN FETCH a.usuario
        WHERE a.usuario.id = :usuarioId
        ORDER BY a.fechaLogin DESC
    """)
   List<AccesoUsuario> findAccesosOrdenadosPorUsuario(@Param("usuarioId") Long usuarioId);



   @Query("""
        SELECT a FROM AccesoUsuario a
        JOIN FETCH a.usuario
        WHERE a.fechaLogin BETWEEN :inicio AND :fin
        ORDER BY a.fechaLogin DESC
    """)
   List<AccesoUsuario> findByRangoFechas(
           @Param("inicio") LocalDateTime inicio,
           @Param("fin") LocalDateTime fin
   );



   List<AccesoUsuario> findByExitosoFalse();


   @Query("""
        SELECT a FROM AccesoUsuario a
        JOIN FETCH a.usuario
        WHERE a.exitoso = false
        ORDER BY a.fechaLogin DESC
    """)
   List<AccesoUsuario> findFailedLoginsWithUser();



   // Usuarios más activos (COUNT + GROUP BY)
   @Query("""
        SELECT a.usuario.id, COUNT(a)
        FROM AccesoUsuario a
        GROUP BY a.usuario.id
        ORDER BY COUNT(a) DESC
    """)
   List<Object[]> usuariosMasActivos();


   // Promedio de sesiones por usuario
   @Query("""
        SELECT a.usuario.id, COUNT(a)
        FROM AccesoUsuario a
        WHERE a.exitoso = true
        GROUP BY a.usuario.id
    """)
   List<Object[]> accesosExitososPorUsuario();




   @Query("""
        SELECT a FROM AccesoUsuario a
        JOIN FETCH a.usuario
        WHERE a.fechaLogout IS NULL
    """)
   List<AccesoUsuario> sesionesActivas();




   @Query("""
        SELECT a FROM AccesoUsuario a
        JOIN FETCH a.usuario
        WHERE a.usuario.id = :usuarioId
        ORDER BY a.fechaLogin DESC
    """)
   List<AccesoUsuario> ultimoAccesoUsuario(@Param("usuarioId") Long usuarioId);
}