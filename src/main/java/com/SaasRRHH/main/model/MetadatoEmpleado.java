package com.SaasRRHH.main.model; 
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "metadatos_empleado",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_meta_empleado",
                        columnNames = {"empleado_id", "clave"}
                )
        },
        indexes = {
                @Index(name = "idx_metadatos_empleado", columnList = "empleado_id")
        }
)
public class MetadatoEmpleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El empleado es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "empleado_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_meta_empleado")
    )
    private Empleado empleado;

    @NotBlank(message = "La clave es obligatoria")
    @Size(max = 50, message = "La clave no puede superar 50 caracteres")
    @Column(name = "clave", nullable = false, length = 50)
    private String clave;

    @Size(max = 250, message = "El valor no puede superar 250 caracteres")
    @Column(name = "valor", length = 250)
    private String valor;
}