    package com.SaasRRHH.main.services.impl;

import com.SaasRRHH.main.DTO.BoletaPagoRequestDTO;
import com.SaasRRHH.main.model.Empleado;
import com.SaasRRHH.main.model.RegistroAsistencia;
import com.SaasRRHH.main.repository.EmpleadoRepository;
import com.SaasRRHH.main.repository.RegistroAsistenciaRepository;
import com.SaasRRHH.main.services.NominaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NominaServiceImpl implements NominaService {

    /** Monto de Asignación Familiar: 10% de la RMV (S/ 1,025.00) */
    private static final BigDecimal ASIGNACION_FAMILIAR_MONTO = new BigDecimal("102.50");

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private RegistroAsistenciaRepository registroAsistenciaRepository;

    @Override
    public BoletaPagoRequestDTO calcularBoleta(Long empleadoId, Integer mes, Integer anio) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado: " + empleadoId));

        BigDecimal sueldoBase = empleado.getSueldoBase() != null ? empleado.getSueldoBase() : BigDecimal.ZERO;

        LocalDate inicioMes = LocalDate.of(anio, mes, 1);
        LocalDate inicioSiguiente = inicioMes.plusMonths(1);
        LocalDateTime inicio = inicioMes.atStartOfDay();
        LocalDateTime fin = inicioSiguiente.atStartOfDay();

        List<RegistroAsistencia> registros = registroAsistenciaRepository.findByEmpleadoIdAndFechaHoraBetween(empleadoId, inicio, fin);

        // Contar días trabajados (entrada validada por día)
        Set<LocalDate> diasConEntrada = new HashSet<>();
        for (RegistroAsistencia r : registros) {
            if (r.getTipoMarcacion() != null && r.getTipoMarcacion().equalsIgnoreCase("ENTRADA")
                    && r.getEstado() != null && r.getEstado().equalsIgnoreCase("VALIDADO")) {
                diasConEntrada.add(r.getFechaHora().toLocalDate());
            }
        }

        int diasTrabajados = diasConEntrada.size();
        int diasLaborables = contarDiasLaborables(inicioMes, inicioSiguiente.minusDays(1));
        if (diasLaborables <= 0) diasLaborables = diasTrabajados > 0 ? diasTrabajados : 1;

        int diasNoTrabajados = Math.max(0, diasLaborables - diasTrabajados);

        BigDecimal dailyRate = BigDecimal.ZERO;
        if (diasLaborables > 0) {
            dailyRate = sueldoBase.divide(BigDecimal.valueOf(diasLaborables), 2, RoundingMode.HALF_UP);
        }

        BigDecimal descuentoInasistencia = dailyRate.multiply(BigDecimal.valueOf(diasNoTrabajados)).setScale(2, RoundingMode.HALF_UP);
        // Evitar que el descuento supere el sueldo base por redondeo
        if (descuentoInasistencia.compareTo(sueldoBase) > 0) {
            descuentoInasistencia = sueldoBase;
        }

        BigDecimal asignacionFamiliar = empleado.getAsignacionFamiliar() != null && empleado.getAsignacionFamiliar()
                ? ASIGNACION_FAMILIAR_MONTO
                : BigDecimal.ZERO;

        BigDecimal bonoBeta = BigDecimal.ZERO;
        BigDecimal horasExtra = BigDecimal.ZERO;
        BigDecimal otrosBonos = BigDecimal.ZERO;
        BigDecimal otrosDescuentos = BigDecimal.ZERO;

        BigDecimal totalIngresos = sueldoBase.add(asignacionFamiliar).add(bonoBeta).add(horasExtra).add(otrosBonos);
        BigDecimal totalDescuentos = descuentoInasistencia.add(otrosDescuentos);
        BigDecimal neto = totalIngresos.subtract(totalDescuentos).setScale(2, RoundingMode.HALF_UP);
        if (neto.compareTo(BigDecimal.ZERO) < 0) {
            neto = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BoletaPagoRequestDTO dto = new BoletaPagoRequestDTO();
        dto.setEmpleadoId(empleadoId);
        dto.setSueldoBase(sueldoBase);
        dto.setDiasTrabajados(diasTrabajados);
        dto.setDiasNoTrabajados(diasNoTrabajados);
        dto.setAsignacionFamiliar(asignacionFamiliar);
        dto.setBonoBeta(bonoBeta);
        dto.setHorasExtraPago(horasExtra);
        dto.setOtrosBonos(otrosBonos);
        dto.setDescuentoInasistencia(descuentoInasistencia);
        dto.setOtrosDescuentos(otrosDescuentos);
        dto.setTotalIngresos(totalIngresos);
        dto.setTotalDescuentos(totalDescuentos);
        dto.setNetoPagar(neto);

        return dto;
    }

    private int contarDiasLaborables(LocalDate inicio, LocalDate finInclusive) {
        int dias = 0;
        LocalDate d = inicio;
        while (!d.isAfter(finInclusive)) {
            switch (d.getDayOfWeek()) {
                case SATURDAY, SUNDAY -> { /* no laboral */ }
                default -> dias++;
            }
            d = d.plusDays(1);
        }
        return dias;
    }

}
