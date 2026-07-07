package com.SaasRRHH.main.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "documentos_privados", indexes = {
                @Index(name = "idx_documento_empleado", columnList = "empleado_id"),
                @Index(name = "idx_tipo", columnList = "tipo_id")
})
public class DocumentoPrivado {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotNull(message = "El empleado es obligatorio")
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "empleado_id", nullable = false, foreignKey = @ForeignKey(name = "fk_doc_empleado"))
        private Empleado empleado;

        @NotNull(message = "El tipo de documento es obligatorio")
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "tipo_id", nullable = false, foreignKey = @ForeignKey(name = "fk_doc_tipo"))
        private TipoDocumento tipo;

        @NotBlank(message = "La URL del archivo es obligatoria")
        @Size(max = 500, message = "La URL no puede superar 500 caracteres")
        @Column(name = "archivo_url", nullable = false, length = 500)
        private String archivoUrl;

        @Column(name = "fecha_vencimiento")
        private LocalDate fechaVencimiento;

        @NotNull(message = "La fecha de emision es obligatoria")
        @Column(name="fecha-emision", nullable = false)
        private LocalDate fecha_emision;

        @Column(name = "fecha_carga", nullable = false, insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
        private LocalDateTime fechaCarga;

        @Column(name = "activo", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
        private Boolean activo = true;
}