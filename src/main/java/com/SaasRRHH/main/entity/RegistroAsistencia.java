package com.SaasRRHH.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "registros_asistencia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroAsistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    /**
     * Valores: ENTRADA, SALIDA
     */
    @Column(name = "tipo_marcacion", length = 20, nullable = false)
    private String tipoMarcacion;

    /**
     * Valores: QR, FACIAL, MANUAL
     */
    @Column(name = "metodo", length = 20)
    private String metodo = "QR";

    /**
     * Valores: VALIDADO, OBSERVADO, RECHAZADO
     */
    @Column(name = "estado", length = 20)
    private String estado = "VALIDADO";

    @Column(name = "observaciones", length = 500)
    private String observaciones;
}
