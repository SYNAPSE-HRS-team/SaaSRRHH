package com.SaasRRHH.main.DTO; 
import jakarta.persistence.*;

import jakarta.validation.constraints.*;
import lombok.Data;



@Data
@Entity
@Table(
        name = "tipos_documento",
        indexes = {
                @Index(name = "idx_nombre", columnList = "nombre")
        }
)
public class TipoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo")
    private Long idTipo;

    @NotBlank(message = "El nombre del tipo de documento es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar 50 caracteres")
    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(name = "obligatorio", nullable = false,
            columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean obligatorio = false;


    @Column(name = "dias_vigencia")
    private Integer diasVigencia;

    @Column(name = "requiere_renovacion", nullable = false,
            columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean requiereRenovacion = false;

    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    @Column(name = "descripcion", length = 255)
    private String descripcion;
}