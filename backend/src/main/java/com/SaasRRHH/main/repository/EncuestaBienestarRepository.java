package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.Encuestabienestar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EncuestaBienestarRepository extends JpaRepository<Encuestabienestar, Long> {

    boolean existsByEmpleadoIdAndFecha(Long empleadoId, LocalDate fecha);

    List<Encuestabienestar> findByEmpleadoIdOrderByFechaDesc(Long empleadoId);

    List<Encuestabienestar> findByFechaBetween(LocalDate inicio, LocalDate fin);

    List<Encuestabienestar> findByEmpleadoIdAndFechaBetween(Long empleadoId, LocalDate inicio, LocalDate fin);

    @Query("SELECT e.empleado.id FROM Encuestabienestar e WHERE e.fecha >= :fechaLimite GROUP BY e.empleado.id HAVING AVG((e.cargaLaboral + e.apoyoEquipo + e.proyeccion)/3.0) < 2.5")
    List<Long> findEmpleadosEnRiesgo(@Param("fechaLimite") LocalDate fechaLimite);

    @Query("SELECT AVG((e.cargaLaboral + e.apoyoEquipo + e.proyeccion)/3.0) FROM Encuestabienestar e WHERE e.fecha BETWEEN :inicio AND :fin")
    Double calcularPromedioGeneral(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}