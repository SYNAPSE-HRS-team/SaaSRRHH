package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.BoletaPagoRequestDTO;
import com.SaasRRHH.main.mapper.BoletaPagoMapper;
import com.SaasRRHH.main.model.BoletaPago;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.Planilla;
import com.SaasRRHH.main.repository.BoletaPagoRepository;
import com.SaasRRHH.main.repository.EmpleadoRepository;
import com.SaasRRHH.main.services.NominaProcessorService;
import com.SaasRRHH.main.services.NominaService;
import com.SaasRRHH.main.services.PlanillaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NominaProcessorServiceImpl implements NominaProcessorService {

    private final EmpleadoRepository empleadoRepository;
    private final NominaService nominaService;
    private final PlanillaService planillaService;
    private final BoletaPagoRepository boletaRepo;

    @Override
    @Transactional
    public Planilla generarPlanilla(Integer mes, Integer anio) {
        Planilla planilla = new Planilla();
        planilla.setMes(mes);
        planilla.setAnio(anio);
        planilla.setEstado(Planilla.EstadoPlanilla.PROCESADO);
        planilla = planillaService.guardar(planilla);

        List<Empleado> empleados = empleadoRepository.findByActivoTrue();
        BigDecimal totalPagado = BigDecimal.ZERO;

        for (Empleado e : empleados) {
            BoletaPagoRequestDTO dto = nominaService.calcularBoleta(e.getId(), mes, anio);
            dto.setPlanillaId(planilla.getId());
            BoletaPago boleta = BoletaPagoMapper.toEntity(dto);
            boleta.setEmpleado(e);
            boleta.setPlanilla(planilla);
            boleta = boletaRepo.save(boleta);
            if (boleta.getNetoPagar() != null) {
                BigDecimal neto = boleta.getNetoPagar();
                if (neto.compareTo(BigDecimal.ZERO) < 0) {
                    neto = BigDecimal.ZERO;
                }
                totalPagado = totalPagado.add(neto);
            }
        }

        planilla.setTotalPagado(totalPagado);
        planilla = planillaService.guardar(planilla);
        return planilla;
    }
}
