package com.SaasRRHH.main.DTO;

import com.SaasRRHH.main.model.Familiar.Parentesco;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FamiliarDTO {

    private Long id;

    private Long empleadoId;

    private Parentesco parentesco;

    private String nombres;

    private String dniFamiliar;

    private LocalDate fechaNacimiento;

    private Boolean estudia;

    private Boolean activo;
}