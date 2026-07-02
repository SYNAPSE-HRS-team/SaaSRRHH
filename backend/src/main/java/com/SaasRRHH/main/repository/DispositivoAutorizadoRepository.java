package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.DispositivoAutorizado;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DispositivoAutorizadoRepository
        extends JpaRepository<DispositivoAutorizado, Long> {

    // ===================================
    // CONSULTAS DERIVADAS
    // ===================================

    List<DispositivoAutorizado>
    findByUsuarioId(Long usuarioId);

    List<DispositivoAutorizado>
    findByActivoTrue();

    Optional<DispositivoAutorizado>
    findByHardwareId(String hardwareId);

    boolean existsByHardwareId(String hardwareId);

    boolean existsByUsuarioIdAndHardwareId(
            Long usuarioId,
            String hardwareId);

    // ===================================
    // JPQL
    // ===================================

    @Query("""
           SELECT d
           FROM DispositivoAutorizado d
           JOIN FETCH d.usuario u
           ORDER BY d.fechaRegistro DESC
           """)
    List<DispositivoAutorizado>
    findAllWithUsuario();

    @Query("""
           SELECT d
           FROM DispositivoAutorizado d
           JOIN FETCH d.usuario u
           WHERE d.activo = true
           ORDER BY d.fechaRegistro DESC
           """)
    List<DispositivoAutorizado>
    listarActivosConUsuario();

    @Query("""
       SELECT d
       FROM DispositivoAutorizado d
       WHERE d.fechaRegistro >= :fecha
       ORDER BY d.fechaRegistro DESC
       """)
    List<DispositivoAutorizado>
    buscarDispositivosRecientes(
            @Param("fecha")
            LocalDateTime fecha);

    @Query("""
           SELECT d.usuario.id,
                  d.usuario.email,
                  COUNT(d)
           FROM DispositivoAutorizado d
           GROUP BY d.usuario.id,
                    d.usuario.email
           HAVING COUNT(d) > 1
           ORDER BY COUNT(d) DESC
           """)
    List<Object[]>
    usuariosConMultiplesDispositivos();

    @Query("""
           SELECT COUNT(d)
           FROM DispositivoAutorizado d
           WHERE d.activo = true
           """)
    Long contarDispositivosActivos();

    @Query("""
           SELECT d
           FROM DispositivoAutorizado d
           WHERE d.activo = false
           ORDER BY d.fechaRegistro DESC
           """)
    List<DispositivoAutorizado>
    dispositivosInactivos();

}