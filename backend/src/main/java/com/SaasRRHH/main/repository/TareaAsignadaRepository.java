package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.TareaAsignada;
import com.SaasRRHH.main.model.TareaAsignada.EstadoTarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TareaAsignadaRepository extends JpaRepository<TareaAsignada, Long> {

    // ============================================
    // CONSULTAS CON LEFT JOIN FETCH (CORREGIDO)
    // ============================================
    
    @Query("SELECT t FROM TareaAsignada t " +
           "LEFT JOIN FETCH t.empleado e " +
           "LEFT JOIN FETCH t.supervisor s " +
           "LEFT JOIN FETCH t.area a")
    List<TareaAsignada> findAllWithRelations();

    @Query("SELECT t FROM TareaAsignada t " +
           "LEFT JOIN FETCH t.empleado e " +
           "LEFT JOIN FETCH t.supervisor s " +
           "LEFT JOIN FETCH t.area a " +
           "WHERE t.empleado.id = :empleadoId")
    List<TareaAsignada> findByEmpleadoIdWithRelations(@Param("empleadoId") Long empleadoId);

    @Query("SELECT t FROM TareaAsignada t " +
           "LEFT JOIN FETCH t.empleado e " +
           "LEFT JOIN FETCH t.supervisor s " +
           "LEFT JOIN FETCH t.area a " +
           "WHERE t.supervisor.id = :supervisorId")
    List<TareaAsignada> findBySupervisorIdWithRelations(@Param("supervisorId") Long supervisorId);

    @Query("SELECT t FROM TareaAsignada t " +
           "LEFT JOIN FETCH t.empleado e " +
           "LEFT JOIN FETCH t.supervisor s " +
           "LEFT JOIN FETCH t.area a " +
           "WHERE t.area.id = :areaId")
    List<TareaAsignada> findByAreaIdWithRelations(@Param("areaId") Long areaId);

    @Query("SELECT t FROM TareaAsignada t " +
           "LEFT JOIN FETCH t.empleado e " +
           "LEFT JOIN FETCH t.supervisor s " +
           "LEFT JOIN FETCH t.area a " +
           "WHERE t.empleado.id = :empleadoId AND t.fecha = :fecha")
    List<TareaAsignada> findByEmpleadoIdAndFechaWithRelations(@Param("empleadoId") Long empleadoId,
                                                               @Param("fecha") LocalDate fecha);

    @Query("SELECT t FROM TareaAsignada t " +
           "LEFT JOIN FETCH t.empleado e " +
           "LEFT JOIN FETCH t.supervisor s " +
           "LEFT JOIN FETCH t.area a " +
           "WHERE t.id = :id")
    Optional<TareaAsignada> findByIdWithRelations(@Param("id") Long id);

    // ============================================
    // CONSULTAS BÁSICAS
    // ============================================

    List<TareaAsignada> findByEmpleadoId(Long empleadoId);
    List<TareaAsignada> findBySupervisorId(Long supervisorId);
    List<TareaAsignada> findByAreaId(Long areaId);
    List<TareaAsignada> findByEstado(EstadoTarea estado);
    List<TareaAsignada> findByEmpleadoIdAndFecha(Long empleadoId, LocalDate fecha);
    List<TareaAsignada> findBySupervisorIdAndEstado(Long supervisorId, EstadoTarea estado);
    long countByEmpleadoIdAndEstado(Long empleadoId, EstadoTarea estado);

    @Query("SELECT t FROM TareaAsignada t WHERE t.area.id = :areaId AND t.estado = :estado")
    List<TareaAsignada> findByAreaAndEstado(@Param("areaId") Long areaId, @Param("estado") EstadoTarea estado);

    @Query("SELECT t FROM TareaAsignada t WHERE t.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<TareaAsignada> findTareasByFechaRange(@Param("fechaInicio") LocalDate fechaInicio,
                                               @Param("fechaFin") LocalDate fechaFin);

    List<TareaAsignada> findByFechaBeforeAndEstadoNot(LocalDate fecha, EstadoTarea estado);

     @Query("SELECT t FROM TareaAsignada t WHERE t.empleado.id = :empleadoId AND t.fecha BETWEEN :inicio AND :fin")
    List<TareaAsignada> findByEmpleadoIdAndFechaBetween(
            @Param("empleadoId") Long empleadoId,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin
    );
}