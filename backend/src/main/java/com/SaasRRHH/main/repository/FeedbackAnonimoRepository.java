package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.FeedbackAnonimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedbackAnonimoRepository extends JpaRepository<FeedbackAnonimo, Long> {

    List<FeedbackAnonimo> findByCategoriaOrderByFechaEnvioDesc(FeedbackAnonimo.CategoriaFeedback categoria);

    List<FeedbackAnonimo> findByEstadoOrderByFechaEnvioDesc(FeedbackAnonimo.EstadoFeedback estado);

    List<FeedbackAnonimo> findByFechaEnvioBetweenOrderByFechaEnvioDesc(LocalDateTime inicio, LocalDateTime fin);

    Long countByEstado(FeedbackAnonimo.EstadoFeedback estado);

    // ============================================
    // ✅ NUEVAS QUERIES PARA FEEDBACK
    // ============================================

    /**
     * Busca feedback por empleado, ordenado por fecha de envío
     */
    @Query("""
       SELECT f
       FROM FeedbackAnonimo f
       LEFT JOIN FETCH f.empleado e
       WHERE f.empleado.id = :empleadoId
       ORDER BY f.fechaEnvio DESC
       """)
    List<FeedbackAnonimo> findByEmpleadoIdOrderByFechaEnvioDesc(@Param("empleadoId") Long empleadoId);

    /**
     * Cuenta feedback pendientes por categoría
     */
    @Query("""
       SELECT f.categoria, COUNT(f)
       FROM FeedbackAnonimo f
       WHERE f.estado = 'PENDIENTE'
       GROUP BY f.categoria
       ORDER BY COUNT(f) DESC
       """)
    List<Object[]> countPendientesPorCategoria();

    /**
     * Busca feedback que han sido respondidos por el admin
     */
    @Query("""
       SELECT f
       FROM FeedbackAnonimo f
       LEFT JOIN FETCH f.empleado e
       WHERE f.respuesta IS NOT NULL
       AND f.estado IN ('REVISADO', 'NO_PROCEDE', 'ACEPTADO')
       ORDER BY f.fechaRespuesta DESC
       """)
    List<FeedbackAnonimo> findRespondidos();

    /**
     * Busca feedback pendientes (sin respuesta del admin)
     */
    @Query("""
       SELECT f
       FROM FeedbackAnonimo f
       LEFT JOIN FETCH f.empleado e
       WHERE f.estado = 'PENDIENTE'
       ORDER BY f.fechaEnvio ASC
       """)
    List<FeedbackAnonimo> findPendientes();

    /**
     * Cuenta feedback por empleado (para ver quiénes envían más)
     */
    @Query("""
       SELECT f.empleado.id, f.empleado.nombres, f.empleado.apellidos, COUNT(f)
       FROM FeedbackAnonimo f
       WHERE f.empleado IS NOT NULL
       GROUP BY f.empleado.id, f.empleado.nombres, f.empleado.apellidos
       ORDER BY COUNT(f) DESC
       """)
    List<Object[]> rankingEmpleadosPorFeedback();

    /**
     * Busca feedback por empleado y estado
     */
    @Query("""
       SELECT f
       FROM FeedbackAnonimo f
       LEFT JOIN FETCH f.empleado e
       WHERE f.empleado.id = :empleadoId
       AND f.estado = :estado
       ORDER BY f.fechaEnvio DESC
       """)
    List<FeedbackAnonimo> findByEmpleadoIdAndEstado(
            @Param("empleadoId") Long empleadoId,
            @Param("estado") FeedbackAnonimo.EstadoFeedback estado);
}