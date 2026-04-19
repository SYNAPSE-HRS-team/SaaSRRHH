package com.SaasRRHH.main.model;

import com.SaasRRHH.main.entity.RegistroAsistencia;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "validaciones_seguridad",
        indexes = {
                @Index(name = "idx_asistencia", columnList = "asistencia_id")
        }
)
public class ValidacionSeguridad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La asistencia es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "asistencia_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_valid_asist")
    )
    private RegistroAsistencia asistencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "dispositivo_id",
            foreignKey = @ForeignKey(name = "fk_valid_dispositivo")
    )
    private DispositivoAutorizado dispositivo;

    @Size(max = 255, message = "El TOTP hash no puede superar 255 caracteres")
    @Column(name = "totp_hash", length = 255)
    private String totpHash;

    @Column(name = "totp_valido", nullable = false,
            columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean totpValido = false;

    @Column(name = "fecha_validacion", nullable = false,
            insertable = false, updatable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaValidacion;
}