package com.SaasRRHH.main.model;

import com.SaasRRHH.main.entity.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "accesos_usuario",
        indexes = {
                @Index(name = "idx_usuario",     columnList = "usuario_id"),
                @Index(name = "idx_fecha_login", columnList = "fecha_login")
        }
)
public class AccesoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acceso")
    private Long idAcceso;

    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "usuario_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_acceso_usuario")
    )
    private Usuario usuario;

    @Column(name = "fecha_login", nullable = false,
            insertable = false, updatable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaLogin;

    @Column(name = "fecha_logout")
    private LocalDateTime fechaLogout;

    @Size(max = 255, message = "User-agent no puede superar 255 caracteres")
    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(name = "exitoso", nullable = false,
            columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean exitoso = true;



}