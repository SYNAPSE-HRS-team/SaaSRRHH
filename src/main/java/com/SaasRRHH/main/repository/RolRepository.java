package com.SaasRRHH.main.repository;
import com.SaasRRHH.main.model.Rol; 
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombreRol(String nombreRol);
    boolean existsByNombreRol(String nombreRol);
}

