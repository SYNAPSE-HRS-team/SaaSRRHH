package com.SaasRRHH.main.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsuarioRequestDTO {

    @NotBlank
    @Email
    private String email;

    private String password;

    @NotNull
    private Long rolId;

    private Boolean activo;

    private String nombre;
    private String apellido;
    private String telefono;
}