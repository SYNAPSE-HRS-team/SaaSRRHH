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
    
    // Buscar tareas por rango de fechas
    @Query("SELECT t FROM TareaAsignada t WHERE t.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<TareaAsignada> findTareasByFechaRange(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);
}