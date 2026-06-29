package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.AreaTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AreaTrabajoRepository extends JpaRepository<AreaTrabajo, Long> {
    
    Optional<AreaTrabajo> findByNombre(String nombre);
    
    boolean existsByNombre(String nombre);
    
    List<AreaTrabajo> findByActivoTrue();
}