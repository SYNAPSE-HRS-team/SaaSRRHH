package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.UsuarioRequestDTO;
import com.SaasRRHH.main.DTO.UsuarioResponseDTO;
import com.SaasRRHH.main.model.Rol;
import com.SaasRRHH.main.model.Usuario;

public class UsuarioMapper {

    // ENTITY → RESPONSE DTO
    public static UsuarioResponseDTO toDTO(Usuario u) {

        UsuarioResponseDTO dto = new UsuarioResponseDTO();

        dto.setId(u.getId());
        dto.setEmail(u.getEmail());

        if (u.getRol() != null) {
            dto.setRolId(u.getRol().getIdRol());
            dto.setRolNombre(u.getRol().getNombreRol());
        }

        dto.setActivo(u.getActivo());
        dto.setFechaCreacion(u.getFechaCreacion());
        dto.setUltimoAcceso(u.getUltimoAcceso());

        return dto;
    }

    // REQUEST DTO → ENTITY
    public static Usuario toEntity(UsuarioRequestDTO dto, Rol rol) {

        Usuario u = new Usuario();

        u.setEmail(dto.getEmail());
        u.setPassword(dto.getPassword()); // luego aquí podrías encriptar
        u.setRol(rol);
        u.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

        return u;
    }
}