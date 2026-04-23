package com.SaasRRHH.main.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "validaciones_seguridad")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidacionSeguridad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asistencia_id")
    private RegistroAsistencia asistencia;

@Column(name = "totp_hash", length = 255)
    private String totpHash;

    @Column(name = "totp_valido")
    private Boolean totpValido = false;

    @Column(name = "fecha_validacion")
    private LocalDateTime fechaValidacion;

    // Remove dispositivo reference since no entity
}
