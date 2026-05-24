package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    Optional<Empleado> findByDni(String dni);
    List<Empleado> findByActivoTrue();
    Optional<Empleado> findByUsuarioId(Long userId);
}