package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.TareaAsignada;
import com.SaasRRHH.main.model.TareaAsignada.EstadoTarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TareaAsignadaRepository extends JpaRepository<TareaAsignada, Long> {

    // Buscar por empleado
    List<TareaAsignada> findByEmpleadoId(Long empleadoId);

    // Buscar por supervisor
    List<TareaAsignada> findBySupervisorId(Long supervisorId);

    // Buscar por área
    List<TareaAsignada> findByAreaId(Long areaId);

    // Buscar por estado
    List<TareaAsignada> findByEstado(EstadoTarea estado);

    // Buscar por empleado y fecha
    List<TareaAsignada> findByEmpleadoIdAndFecha(Long empleadoId, LocalDate fecha);

    // Buscar por supervisor y estado
    List<TareaAsignada> findBySupervisorIdAndEstado(Long supervisorId, EstadoTarea estado);

    // Contar tareas por empleado en estado pendiente
    long countByEmpleadoIdAndEstado(Long empleadoId, EstadoTarea estado);

    // Filtra por estado y tarea asignada a un área específica
    @Query("SELECT t FROM TareaAsignada t WHERE t.area.id = :areaId AND t.estado = :estado")
    List<TareaAsignada> findByAreaAndEstado(@Param("areaId") Long areaId, @Param("estado") EstadoTarea estado);

    // Buscar tareas por rango de fechas
    @Query("SELECT t FROM TareaAsignada t WHERE t.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<TareaAsignada> findTareasByFechaRange(@Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT t FROM TareaAsignada t " +
            "JOIN FETCH t.empleado e " +
            "JOIN FETCH t.supervisor s " +
            "JOIN FETCH t.area a")
    List<TareaAsignada> findAllWithRelations();

    @Query("SELECT t FROM TareaAsignada t " +
            "JOIN FETCH t.empleado e " +
            "JOIN FETCH t.supervisor s " +
            "JOIN FETCH t.area a " +
            "WHERE t.empleado.id = :empleadoId")
    List<TareaAsignada> findByEmpleadoIdWithRelations(@Param("empleadoId") Long empleadoId);

    // ✅ Trae tareas de un supervisor con todos sus datos
    @Query("SELECT t FROM TareaAsignada t " +
            "JOIN FETCH t.empleado e " +
            "JOIN FETCH t.supervisor s " +
            "JOIN FETCH t.area a " +
            "WHERE t.supervisor.id = :supervisorId")
    List<TareaAsignada> findBySupervisorIdWithRelations(@Param("supervisorId") Long supervisorId);

    // ✅ Trae tareas de un área con todos sus datos
    @Query("SELECT t FROM TareaAsignada t " +
            "JOIN FETCH t.empleado e " +
            "JOIN FETCH t.supervisor s " +
            "JOIN FETCH t.area a " +
            "WHERE t.area.id = :areaId")
    List<TareaAsignada> findByAreaIdWithRelations(@Param("areaId") Long areaId);

    // ✅ Trae tareas de un empleado en una fecha específica
    @Query("SELECT t FROM TareaAsignada t " +
            "JOIN FETCH t.empleado e " +
            "JOIN FETCH t.supervisor s " +
            "JOIN FETCH t.area a " +
            "WHERE t.empleado.id = :empleadoId AND t.fecha = :fecha")
    List<TareaAsignada> findByEmpleadoIdAndFechaWithRelations(@Param("empleadoId") Long empleadoId,
                                                              @Param("fecha") LocalDate fecha);

    List<TareaAsignada> findByFechaBeforeAndEstadoNot(LocalDate fecha, EstadoTarea estado);

}