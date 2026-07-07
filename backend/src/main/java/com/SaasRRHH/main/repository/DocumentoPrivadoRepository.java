package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.DocumentoPrivado;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DocumentoPrivadoRepository
        extends JpaRepository<DocumentoPrivado, Long> {

    // ===================================
    // CONSULTAS DERIVADAS
    // ===================================

    List<DocumentoPrivado>
    findByActivoTrue();

    List<DocumentoPrivado>
    findByEmpleadoId(Long empleadoId);

    List<DocumentoPrivado>
       findByTipoIdTipo(Long tipoId);

    // ===================================
    // JPQL
    // ===================================

    @Query("""
           SELECT d
           FROM DocumentoPrivado d
           JOIN FETCH d.empleado e
           JOIN FETCH d.tipo t
           WHERE d.activo = true
           ORDER BY d.fechaCarga DESC
           """)
    List<DocumentoPrivado>
    listarActivosConRelaciones();

    @Query("""
    SELECT d 
    FROM DocumentoPrivado d
    JOIN FETCH d.empleado e
    JOIN FETCH d.tipo t
    WHERE d.activo = true 
    AND d.fecha-emision = :fechaEmision
    ORDER BY d.fechaCarga DESC
""")
List<DocumentoPrivado>
    listarPorFechaEmision(@Param("fechaEmision") LocalDate fechaEmision);

    @Query("""
           SELECT d
           FROM DocumentoPrivado d
           JOIN FETCH d.empleado e
           JOIN FETCH d.tipo t
           WHERE d.fechaVencimiento IS NOT NULL
           AND d.fechaVencimiento < CURRENT_DATE
           ORDER BY d.fechaVencimiento ASC
           """)
    List<DocumentoPrivado>
    documentosVencidos();

    @Query("""
           SELECT d
           FROM DocumentoPrivado d
           JOIN FETCH d.empleado e
           JOIN FETCH d.tipo t
           WHERE d.fechaVencimiento
           BETWEEN CURRENT_DATE AND :fechaLimite
           ORDER BY d.fechaVencimiento ASC
           """)
    List<DocumentoPrivado>
    documentosPorVencer(
            @Param("fechaLimite")
            LocalDate fechaLimite);

    @Query("""
           SELECT t.nombre, COUNT(d)
           FROM DocumentoPrivado d
           JOIN d.tipo t
           GROUP BY t.nombre
           ORDER BY COUNT(d) DESC
           """)
    List<Object[]>
    contarDocumentosPorTipo();



    @Query("""
           SELECT e.nombres, e.apellidos, COUNT(d)
           FROM DocumentoPrivado d
           JOIN d.empleado e
           GROUP BY e.nombres, e.apellidos
           ORDER BY COUNT(d) DESC
           """)
    List<Object[]>
    empleadosConMasDocumentos();
}