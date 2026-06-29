package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.UsuarioRequestDTO;
import com.SaasRRHH.main.DTO.UsuarioResponseDTO;
import com.SaasRRHH.main.model.Rol;
import com.SaasRRHH.main.model.Usuario;

public class UsuarioMapper {

    public static UsuarioResponseDTO toDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setEmail(usuario.getEmail());
        dto.setActivo(usuario.getActivo());
        dto.setFechaCreacion(usuario.getFechaCreacion());
        dto.setUltimoAcceso(usuario.getUltimoAcceso());
        
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setTelefono(usuario.getTelefono());

        if (usuario.getRol() != null) {
            // ✅ CORREGIDO: usa getIdRol() en lugar de getId()
            dto.setRolId(usuario.getRol().getIdRol());
            dto.setRolNombre(usuario.getRol().getNombreRol());
        }

        return dto;
    }

    public static Usuario toEntity(UsuarioRequestDTO dto, Rol rol) {
        if (dto == null) {
            return null;
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setRol(rol);
        usuario.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setTelefono(dto.getTelefono());

        return usuario;
    }

    public static void updateEntity(Usuario usuario, UsuarioRequestDTO dto, Rol rol) {
        if (dto == null) {
            return;
        }

        usuario.setEmail(dto.getEmail());
        usuario.setRol(rol);
        
        if (dto.getActivo() != null) {
            usuario.setActivo(dto.getActivo());
        }
        
        if (dto.getNombre() != null) {
            usuario.setNombre(dto.getNombre());
        }
        if (dto.getApellido() != null) {
            usuario.setApellido(dto.getApellido());
        }
        if (dto.getTelefono() != null) {
            usuario.setTelefono(dto.getTelefono());
        }
    }
}