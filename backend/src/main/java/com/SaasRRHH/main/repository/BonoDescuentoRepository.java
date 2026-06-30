package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.BonoDescuento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BonoDescuentoRepository extends JpaRepository<BonoDescuento, Long> {

    @Query("SELECT b FROM BonoDescuento b WHERE b.empleado.id = :empleadoId AND b.mes = :mes AND b.anio = :anio")
    List<BonoDescuento> findByEmpleadoAndPeriodo(@Param("empleadoId") Long empleadoId, @Param("mes") Integer mes, @Param("anio") Integer anio);

    @Query("SELECT b FROM BonoDescuento b WHERE b.mes = :mes AND b.anio = :anio")
    List<BonoDescuento> findByPeriodo(@Param("mes") Integer mes, @Param("anio") Integer anio);
}
