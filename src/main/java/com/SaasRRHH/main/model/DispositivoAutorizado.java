package com.SaasRRHH.main.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import java.time.LocalDateTime;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "dispositivos_autorizados",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_usuario_hardware",
                        columnNames = {"usuario_id", "hardware_id"}
                )
        },
        indexes = {
                @Index(name = "idx_dispositivo_usuario", columnList = "usuario_id")
        }
)
public class DispositivoAutorizado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "usuario_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_disp_usuario")
    )
    private Usuario usuario;

    @NotBlank(message = "El hardware ID es obligatorio")
    @Size(max = 100, message = "El hardware ID no puede superar 100 caracteres")
    @Column(name = "hardware_id", nullable = false, length = 100)
    private String hardwareId;

    @Size(max = 500, message = "El FCM token no puede superar 500 caracteres")
    @Column(name = "fcm_token", length = 500)
    private String fcmToken;

    @Column(name = "activo", nullable = false,
            columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean activo = true;

    @Column(name = "fecha_registro", nullable = false,
            insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaRegistro;


}