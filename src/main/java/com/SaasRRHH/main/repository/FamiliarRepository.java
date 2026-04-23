package com.SaasRRHH.main.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SaasRRHH.main.model.Familiar;


@Repository
public interface FamiliarRepository extends JpaRepository<Familiar, Long> {

}
