package com.SaasRRHH.main.mapper;

import com.SaasRRHH.main.DTO.PlanillaRequestDTO;
import com.SaasRRHH.main.DTO.PlanillaResponseDTO;
import com.SaasRRHH.main.model.Planilla;

public class PlanillaMapper {

    public static Planilla toEntity(PlanillaRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Planilla planilla = new Planilla();
        planilla.setMes(dto.getMes());
        planilla.setAnio(dto.getAnio());
        planilla.setTotalPagado(dto.getTotalPagado());
        if (dto.getEstado() != null) {
            planilla.setEstado(Planilla.EstadoPlanilla.valueOf(dto.getEstado()));
        }
        planilla.setFechaCierre(dto.getFechaCierre());
        return planilla;
    }

    public static PlanillaResponseDTO toDTO(Planilla planilla) {
        if (planilla == null) {
            return null;
        }

        PlanillaResponseDTO dto = new PlanillaResponseDTO();
        dto.setId(planilla.getId());
        dto.setMes(planilla.getMes());
        dto.setAnio(planilla.getAnio());
        dto.setTotalPagado(planilla.getTotalPagado());
        dto.setEstado(planilla.getEstado() != null ? planilla.getEstado().name() : null);
        dto.setFechaCierre(planilla.getFechaCierre());
        return dto;
    }
}