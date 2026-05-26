package com.SaasRRHH.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SaasRRHH.main.model.ValidacionSeguridad;

@Repository
public interface ValidacionSeguridadRepository extends JpaRepository<ValidacionSeguridad, Long> {
    @Query("SELECT v FROM ValidacionSeguridad v JOIN FETCH v.asistencia a JOIN FETCH a.empleado e JOIN FETCH e.usuario LEFT JOIN FETCH v.dispositivo")
    List<ValidacionSeguridad> findAllWithRelaciones();

    @Query("SELECT v FROM ValidacionSeguridad v JOIN FETCH v.asistencia a JOIN FETCH a.empleado e JOIN FETCH e.usuario LEFT JOIN FETCH v.dispositivo WHERE v.id = :id")
    Optional<ValidacionSeguridad> findByIdWithRelaciones(@Param("id") Long id);


    List<ValidacionSeguridad>
    findByTotpValido(Boolean valido);

    @Query("""
   SELECT v
   FROM ValidacionSeguridad v
   ORDER BY v.fechaValidacion DESC
   """)
    List<ValidacionSeguridad> recientes();


    @Query("""
   SELECT v
   FROM ValidacionSeguridad v
   JOIN FETCH v.asistencia a
   JOIN FETCH a.empleado e
   LEFT JOIN FETCH v.dispositivo
   WHERE e.id = :empleadoId
   ORDER BY v.fechaValidacion DESC
   """)
    List<ValidacionSeguridad>
    buscarPorEmpleado(
            @Param("empleadoId")
            Long empleadoId);

    @Query("""
   SELECT v
   FROM ValidacionSeguridad v
   WHERE v.totpValido = false
   ORDER BY v.fechaValidacion DESC
   """)
    List<ValidacionSeguridad>
    intentosFallidos();
}
