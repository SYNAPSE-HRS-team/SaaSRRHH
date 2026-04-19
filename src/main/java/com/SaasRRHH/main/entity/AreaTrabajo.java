package com.SaasRRHH.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "areas_trabajo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", length = 150, nullable = false, unique = true)
    private String nombre;

    @Column(name = "cultivo_tipo", length = 100)
    private String cultivoTipo;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();
}
