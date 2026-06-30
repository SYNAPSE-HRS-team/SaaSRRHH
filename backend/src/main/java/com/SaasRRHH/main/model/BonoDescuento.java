package com.SaasRRHH.main.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bonos_descuentos", indexes = {@Index(name = "idx_bono_empleado", columnList = "empleado_id")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BonoDescuento {

    public enum Tipo { BONO, DESCUENTO }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 20)
    private Tipo tipo;

    @Column(name = "monto", precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "motivo", length = 500)
    private String motivo;

    @Column(name = "mes")
    private Integer mes;

    @Column(name = "anio")
    private Integer anio;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(name = "aplicado")
    private Boolean aplicado = false;
}
