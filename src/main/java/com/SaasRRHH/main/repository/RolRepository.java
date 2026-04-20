package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
}