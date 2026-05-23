package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.RegistroAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroAsistenciaRepository extends JpaRepository<RegistroAsistencia, Long> {
    
    // Buscar por empleado
    List<RegistroAsistencia> findByEmpleadoId(Long empleadoId);
    
    // Buscar por empleado y fecha
    List<RegistroAsistencia> findByEmpleadoIdAndFechaHoraBetween(Long empleadoId, LocalDateTime inicio, LocalDateTime fin);
    
    // Buscar registros de un día específico
    @Query("SELECT r FROM RegistroAsistencia r WHERE r.empleado.id = :empleadoId AND DATE(r.fechaHora) = :fecha")
    List<RegistroAsistencia> findByEmpleadoIdAndFecha(@Param("empleadoId") Long empleadoId, @Param("fecha") LocalDate fecha);
    
    // Buscar última marcación de un empleado
    @Query("SELECT r FROM RegistroAsistencia r WHERE r.empleado.id = :empleadoId ORDER BY r.fechaHora DESC")
    Optional<RegistroAsistencia> findUltimaMarcacionByEmpleadoId(@Param("empleadoId") Long empleadoId);
    
    // Buscar por estado
    List<RegistroAsistencia> findByEstado(String estado);
    
    // Contar asistencias por empleado en un rango de fechas
    long countByEmpleadoIdAndFechaHoraBetween(Long empleadoId, LocalDateTime inicio, LocalDateTime fin);
}