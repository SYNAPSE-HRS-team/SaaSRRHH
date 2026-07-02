package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.MetricaBurnoutRequestDTO;
import com.SaasRRHH.main.DTO.MetricaBurnoutResponseDTO;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.MetricaBurnout;

public class MetricaBurnoutMapper {

    public static MetricaBurnout toEntity(MetricaBurnoutRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        MetricaBurnout metrica = new MetricaBurnout();
        if (dto.getEmpleadoId() != null) {
            Empleado empleado = new Empleado();
            empleado.setId(dto.getEmpleadoId());
            metrica.setEmpleado(empleado);
        }
        if (dto.getNivelRiesgo() != null) {
            metrica.setNivelRiesgo(MetricaBurnout.NivelRiesgoBurnout.valueOf(dto.getNivelRiesgo()));
        }
        metrica.setHorasExtraAcumuladas(dto.getHorasExtraAcumuladas());
        metrica.setTendenciaTardanza(dto.getTendenciaTardanza());
        return metrica;
    }

    public static MetricaBurnoutResponseDTO toDTO(MetricaBurnout metrica) {
        if (metrica == null) {
            return null;
        }

        MetricaBurnoutResponseDTO dto = new MetricaBurnoutResponseDTO();
        dto.setId(metrica.getId());
        
        if (metrica.getEmpleado() != null) {
            Empleado empleado = metrica.getEmpleado();
            dto.setEmpleadoId(empleado.getId());
            
            // ✅ CONCATENAR NOMBRE + APELLIDO
            String nombreCompleto = (empleado.getNombres() != null ? empleado.getNombres() : "") + " " +
                                    (empleado.getApellidos() != null ? empleado.getApellidos() : "");
            dto.setNombreEmpleado(nombreCompleto.trim());
        }
        
        dto.setNivelRiesgo(metrica.getNivelRiesgo() != null ? metrica.getNivelRiesgo().name() : null);
        dto.setHorasExtraAcumuladas(metrica.getHorasExtraAcumuladas());
        dto.setTendenciaTardanza(metrica.getTendenciaTardanza());
        dto.setFechaEvaluacion(metrica.getFechaEvaluacion());
        return dto;
    }
}