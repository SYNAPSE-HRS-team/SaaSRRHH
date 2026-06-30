package com.SaasRRHH.main.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "validaciones_seguridad", indexes = {
        @Index(name = "idx_asistencia", columnList = "asistencia_id"),
        @Index(name = "idx_validaciones_dispositivo", columnList = "dispositivo_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidacionSeguridad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK: asistencia_id NOT NULL
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asistencia_id", nullable = false, foreignKey = @ForeignKey(name = "fk_valid_asist"))
    private RegistroAsistencia asistencia;

    // FK: dispositivo_id NULLABLE
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispositivo_id", foreignKey = @ForeignKey(name = "fk_valid_dispositivo"))
    private DispositivoAutorizado dispositivo;

    @Size(max = 255)
    @Column(name = "totp_hash", length = 255)
    private String totpHash;

    @Column(name = "totp_valido", nullable = false)
    private Boolean totpValido = false;

    @CreationTimestamp
    @Column(name = "fecha_validacion", updatable = false, nullable = false)
    private LocalDateTime fechaValidacion;
}