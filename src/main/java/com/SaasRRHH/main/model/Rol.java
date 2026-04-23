package com.SaasRRHH.main.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;
    
    @Column(name = "nombre_rol", length = 30, nullable = false, unique = true)
    private String nombreRol; // ADMIN, SUPERVISOR, TRABAJADOR
    
    @Column(name = "descripcion", length = 250)
    private String descripcion;
}