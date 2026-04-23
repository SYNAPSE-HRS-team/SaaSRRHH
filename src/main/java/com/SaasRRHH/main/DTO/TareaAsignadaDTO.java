package com.SaasRRHH.main.DTO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.SaasRRHH.main.model.AreaTrabajo;
import com.SaasRRHH.main.model.Empleado;

@Entity
@Table(name = "tareas_asignadas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TareaAsignadaDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", nullable = false)
    private Empleado supervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private AreaTrabajo area;

    /**
     * Valores permitidos: CULTIVADOR, ROCIADOR, ARADOR, RECOLECTOR, LIMPIADOR
     */
    @Column(name = "funcion", length = 30, nullable = false)
    private String funcion;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    /**
     * Valores: PENDIENTE, EN_PROGRESO, COMPLETADO, CANCELADO
     */
    @Column(name = "estado", length = 20)
    private String estado = "PENDIENTE";

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();
}
