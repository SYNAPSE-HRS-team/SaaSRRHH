package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.EncuestaBienestarRequestDTO;
import com.SaasRRHH.main.DTO.EncuestaBienestarResponseDTO;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.Encuestabienestar;

public class EncuestaBienestarMapper {

    public static Encuestabienestar toEntity(EncuestaBienestarRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Encuestabienestar encuesta = new Encuestabienestar();

        if (dto.getEmpleadoId() != null) {
            Empleado empleado = new Empleado();
            empleado.setId(dto.getEmpleadoId());
            encuesta.setEmpleado(empleado);
        }

        encuesta.setFecha(dto.getFecha());
        encuesta.setCargaLaboral(dto.getCargaLaboral());
        encuesta.setApoyoEquipo(dto.getApoyoEquipo());
        encuesta.setProyeccion(dto.getProyeccion());
        return encuesta;
    }

    public static EncuestaBienestarResponseDTO toDTO(Encuestabienestar encuesta) {
        if (encuesta == null) {
            return null;
        }

        EncuestaBienestarResponseDTO dto = new EncuestaBienestarResponseDTO();
        dto.setId(encuesta.getId());
        dto.setEmpleadoId(encuesta.getEmpleado() != null ? encuesta.getEmpleado().getId() : null);
        dto.setFecha(encuesta.getFecha());
        dto.setCargaLaboral(encuesta.getCargaLaboral());
        dto.setApoyoEquipo(encuesta.getApoyoEquipo());
        dto.setProyeccion(encuesta.getProyeccion());
        // Nombre del empleado
        if (encuesta.getEmpleado() != null) {
            dto.setNombreEmpleado(encuesta.getEmpleado().getNombres() + " " + encuesta.getEmpleado().getApellidos());
        }

        // Promedio y nivel de bienestar
        Double promedio = calcularPromedio(encuesta.getCargaLaboral(), encuesta.getApoyoEquipo(),
                encuesta.getProyeccion());
        dto.setPromedioGeneral(promedio);
        dto.setNivelBienestar(clasificarNivel(promedio));
        return dto;
    }

    private static Double calcularPromedio(Integer carga, Integer apoyo, Integer proyeccion) {
        if (carga == null || apoyo == null || proyeccion == null)
            return null;
        double prom = (carga + apoyo + proyeccion) / 3.0;
        return Math.round(prom * 100.0) / 100.0;
    }

    private static String clasificarNivel(Double promedio) {
        if (promedio == null)
            return null;
        if (promedio >= 4.0)
            return "BUENO";
        if (promedio >= 2.5)
            return "REGULAR";
        return "CRITICO";
    }
}