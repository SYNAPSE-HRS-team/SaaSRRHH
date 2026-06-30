package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.BoletaPagoRequestDTO;
import com.SaasRRHH.main.DTO.BoletaPagoResponseDTO;
import com.SaasRRHH.main.model.BoletaPago;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.Planilla;

public class BoletaPagoMapper {

    public static BoletaPago toEntity(BoletaPagoRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        BoletaPago boleta = new BoletaPago();

        if (dto.getEmpleadoId() != null) {
            Empleado empleado = new Empleado();
            empleado.setId(dto.getEmpleadoId());
            boleta.setEmpleado(empleado);
        }

        if (dto.getPlanillaId() != null) {
            Planilla planilla = new Planilla();
            planilla.setId(dto.getPlanillaId());
            boleta.setPlanilla(planilla);
        }

        boleta.setSueldoBase(dto.getSueldoBase());
        boleta.setDiasTrabajados(dto.getDiasTrabajados());
        boleta.setDiasNoTrabajados(dto.getDiasNoTrabajados());
        boleta.setAsignacionFamiliar(dto.getAsignacionFamiliar());
        boleta.setBonoBeta(dto.getBonoBeta());
        boleta.setHorasExtraPago(dto.getHorasExtraPago());
        boleta.setOtrosBonos(dto.getOtrosBonos());
        boleta.setDescuentoInasistencia(dto.getDescuentoInasistencia());
        boleta.setOtrosDescuentos(dto.getOtrosDescuentos());
        boleta.setTotalIngresos(dto.getTotalIngresos());
        boleta.setTotalDescuentos(dto.getTotalDescuentos());
        boleta.setNetoPagar(dto.getNetoPagar());
        return boleta;
    }

    public static BoletaPagoResponseDTO toDTO(BoletaPago boleta) {
        if (boleta == null) {
            return null;
        }

        BoletaPagoResponseDTO dto = new BoletaPagoResponseDTO();
        dto.setId(boleta.getId());
        dto.setEmpleadoId(boleta.getEmpleado() != null ? boleta.getEmpleado().getId() : null);
        if (boleta.getEmpleado() != null) {
            String nombre = (boleta.getEmpleado().getNombres() != null ? boleta.getEmpleado().getNombres() : "")
                    + " " + (boleta.getEmpleado().getApellidos() != null ? boleta.getEmpleado().getApellidos() : "");
            dto.setEmpleadoNombre(nombre.isBlank() ? null : nombre.trim());
        }
        dto.setPlanillaId(boleta.getPlanilla() != null ? boleta.getPlanilla().getId() : null);
        dto.setPlanillaMes(boleta.getPlanilla() != null ? boleta.getPlanilla().getMes() : null);
        dto.setPlanillaAnio(boleta.getPlanilla() != null ? boleta.getPlanilla().getAnio() : null);
        dto.setSueldoBase(boleta.getSueldoBase());
        dto.setDiasTrabajados(boleta.getDiasTrabajados());
        dto.setDiasNoTrabajados(boleta.getDiasNoTrabajados());
        dto.setAsignacionFamiliar(boleta.getAsignacionFamiliar());
        dto.setBonoBeta(boleta.getBonoBeta());
        dto.setHorasExtraPago(boleta.getHorasExtraPago());
        dto.setOtrosBonos(boleta.getOtrosBonos());
        dto.setDescuentoInasistencia(boleta.getDescuentoInasistencia());
        dto.setOtrosDescuentos(boleta.getOtrosDescuentos());
        dto.setTotalIngresos(boleta.getTotalIngresos());
        dto.setTotalDescuentos(boleta.getTotalDescuentos());
        dto.setNetoPagar(boleta.getNetoPagar());
        dto.setFechaEmision(boleta.getFechaEmision());
        return dto;
    }
}