package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.MetricaBurnoutRequestDTO;
import com.SaasRRHH.main.DTO.MetricaBurnoutResponseDTO;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.MetricaBurnout;

public class MetricaBurnoutMapper {

    public static MetricaBurnout toEntity(MetricaBurnoutRequestDTO dto, Empleado empleado) {
        MetricaBurnout metrica = new MetricaBurnout();
        metrica.setEmpleado(empleado);
        metrica.setHorasExtraAcumuladas(dto.getHorasExtraAcumuladas() != null ? dto.getHorasExtraAcumuladas() : 0);
        metrica.setTendenciaTardanza(dto.getTendenciaTardanza() != null ? dto.getTendenciaTardanza() : false);
        
        if (dto.getNivelRiesgo() != null) {
            metrica.setNivelRiesgo(MetricaBurnout.NivelRiesgoBurnout.valueOf(dto.getNivelRiesgo()));
        }
        
        metrica.setFaltasPeriodo(dto.getFaltasPeriodo() != null ? dto.getFaltasPeriodo() : 0);
        metrica.setTardanzasPeriodo(dto.getTardanzasPeriodo() != null ? dto.getTardanzasPeriodo() : 0);
        metrica.setPatronDetectado(dto.getPatronDetectado());
        metrica.setIndicePuntualidad(dto.getIndicePuntualidad() != null ? dto.getIndicePuntualidad() : 100.0);
        metrica.setDiasTrabajados(dto.getDiasTrabajados() != null ? dto.getDiasTrabajados() : 0);
        metrica.setHorasReales(dto.getHorasReales() != null ? dto.getHorasReales() : 0);
        metrica.setHorasContrato(dto.getHorasContrato() != null ? dto.getHorasContrato() : 0);
        
        return metrica;
    }

    public static MetricaBurnoutResponseDTO toDTO(MetricaBurnout metrica) {
        MetricaBurnoutResponseDTO dto = new MetricaBurnoutResponseDTO();
        dto.setId(metrica.getId());
        dto.setNivelRiesgo(metrica.getNivelRiesgo() != null ? metrica.getNivelRiesgo().name() : "BAJO");
        dto.setHorasExtraAcumuladas(metrica.getHorasExtraAcumuladas());
        dto.setTendenciaTardanza(metrica.getTendenciaTardanza());
        dto.setFechaEvaluacion(metrica.getFechaEvaluacion());
        
        // ✅ CORREGIDO: Manejar valores null de la BD
        dto.setFaltasPeriodo(metrica.getFaltasPeriodo() != null ? metrica.getFaltasPeriodo() : 0);
        dto.setTardanzasPeriodo(metrica.getTardanzasPeriodo() != null ? metrica.getTardanzasPeriodo() : 0);
        dto.setPatronDetectado(metrica.getPatronDetectado());
        dto.setIndicePuntualidad(metrica.getIndicePuntualidad() != null ? metrica.getIndicePuntualidad() : 100.0);
        dto.setDiasTrabajados(metrica.getDiasTrabajados() != null ? metrica.getDiasTrabajados() : 0);
        dto.setHorasReales(metrica.getHorasReales() != null ? metrica.getHorasReales() : 0);
        dto.setHorasContrato(metrica.getHorasContrato() != null ? metrica.getHorasContrato() : 0);
        
        dto.setRecomendaciones(generarRecomendaciones(metrica));
        
        if (metrica.getEmpleado() != null) {
            dto.setEmpleadoId(metrica.getEmpleado().getId());
            dto.setNombreEmpleado(metrica.getEmpleado().getNombres() + " " + metrica.getEmpleado().getApellidos());
            dto.setCargo(metrica.getEmpleado().getCargo());
            dto.setDniEmpleado(metrica.getEmpleado().getDni());
            dto.setFechaInicioContrato(metrica.getEmpleado().getFechaInicioContrato());
        }
        
        return dto;
    }
    
    // ✅ CORREGIDO: Manejar valores null
    private static String generarRecomendaciones(MetricaBurnout metrica) {
        StringBuilder sb = new StringBuilder();
        
        // Usar 0 como valor por defecto si es null
        int faltas = metrica.getFaltasPeriodo() != null ? metrica.getFaltasPeriodo() : 0;
        int tardanzas = metrica.getTardanzasPeriodo() != null ? metrica.getTardanzasPeriodo() : 0;
        int horasExtra = metrica.getHorasExtraAcumuladas() != null ? metrica.getHorasExtraAcumuladas() : 0;
        boolean tendencia = metrica.getTendenciaTardanza() != null && metrica.getTendenciaTardanza();
        
        if (metrica.getNivelRiesgo() == MetricaBurnout.NivelRiesgoBurnout.ALTO) {
            sb.append("⚠️ INTERVENCIÓN INMEDIATA: ");
            if (faltas > 3) {
                sb.append("Alto índice de faltas. ");
            }
            if (tardanzas > 5) {
                sb.append("Tardanzas frecuentes. ");
            }
            if (metrica.getPatronDetectado() != null) {
                sb.append("Patrón detectado: ").append(metrica.getPatronDetectado()).append(". ");
            }
            sb.append("Se recomienda reunión con RRHH.");
        } else if (metrica.getNivelRiesgo() == MetricaBurnout.NivelRiesgoBurnout.MEDIO) {
            sb.append("⚠️ SEGUIMIENTO: ");
            if (tendencia) {
                sb.append("Monitorear puntualidad. ");
            }
            if (horasExtra > 20) {
                sb.append("Evaluar carga laboral. ");
            }
        } else {
            sb.append("✅ Sin alertas significativas. Mantener seguimiento regular.");
        }
        
        return sb.toString();
    }
}