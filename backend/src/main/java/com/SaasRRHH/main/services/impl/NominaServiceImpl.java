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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class NominaServiceImpl implements NominaService {

    private static final BigDecimal ASIGNACION_FAMILIAR_MONTO = new BigDecimal("102.50");
    private static final int HORAS_JORNADA_DIARIA = 8;

    // Horas extras: 25% adicional las primeras 2 horas, 35% adicional las siguientes
    private static final BigDecimal TARIFA_HORA_EXTRA_25 = new BigDecimal("1.25");
    private static final BigDecimal TARIFA_HORA_EXTRA_35 = new BigDecimal("1.35");

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

        List<RegistroAsistencia> registros = registroAsistenciaRepository
                .findByEmpleadoIdAndFechaHoraBetween(empleadoId, inicio, fin);


        Set<LocalDate> diasLaborables = new HashSet<>();
        for (int day = 1; day <= inicioMes.lengthOfMonth(); day++) {
            LocalDate fecha = LocalDate.of(anio, mes, day);
            if (esDiaLaborable(fecha)) {
                diasLaborables.add(fecha);
            }
        }

        int diasLaborablesCount = diasLaborables.size();
        if (diasLaborablesCount <= 0) {
            throw new IllegalArgumentException("No hay días laborables en el mes " + mes + "/" + anio);
        }

        int totalHorasLaborables = diasLaborablesCount * HORAS_JORNADA_DIARIA;

        BigDecimal tarifaPorHora = sueldoBase.divide(
                BigDecimal.valueOf(totalHorasLaborables),
                4,
                RoundingMode.HALF_UP
        );

        // Tarifa por hora extra (25% y 35% más)
        BigDecimal tarifaHoraExtra25 = tarifaPorHora.multiply(TARIFA_HORA_EXTRA_25);
        BigDecimal tarifaHoraExtra35 = tarifaPorHora.multiply(TARIFA_HORA_EXTRA_35);



        Map<LocalDate, List<RegistroAsistencia>> registrosPorDia = new HashMap<>();
        for (RegistroAsistencia r : registros) {
            LocalDate fecha = r.getFechaHora().toLocalDate();
            registrosPorDia.computeIfAbsent(fecha, k -> new ArrayList<>()).add(r);
        }



        BigDecimal totalHorasNormales = BigDecimal.ZERO;
        BigDecimal totalHorasExtras = BigDecimal.ZERO;
        BigDecimal totalHorasNoTrabajadas = BigDecimal.ZERO;
        BigDecimal totalHorasTardanza = BigDecimal.ZERO;
        BigDecimal totalHorasSalidaTemprana = BigDecimal.ZERO;

        int diasCompletos = 0;
        int diasMedios = 0;

        for (LocalDate fecha : diasLaborables) {
            List<RegistroAsistencia> registrosDelDia = registrosPorDia.getOrDefault(fecha, new ArrayList<>());

            // Buscar entrada y salida VALIDADAS
            RegistroAsistencia entrada = registrosDelDia.stream()
                    .filter(r -> r.getTipoMarcacion().equalsIgnoreCase("ENTRADA")
                            && r.getEstado().equalsIgnoreCase("VALIDADO"))
                    .findFirst()
                    .orElse(null);

            RegistroAsistencia salida = registrosDelDia.stream()
                    .filter(r -> r.getTipoMarcacion().equalsIgnoreCase("SALIDA")
                            && r.getEstado().equalsIgnoreCase("VALIDADO"))
                    .findFirst()
                    .orElse(null);

            // Buscar entrada RECHAZADA (tardanza)
            RegistroAsistencia entradaRechazada = registrosDelDia.stream()
                    .filter(r -> r.getTipoMarcacion().equalsIgnoreCase("ENTRADA")
                            && r.getEstado().equalsIgnoreCase("RECHAZADO"))
                    .findFirst()
                    .orElse(null);

            // Buscar salida RECHAZADA (salida temprana)
            RegistroAsistencia salidaRechazada = registrosDelDia.stream()
                    .filter(r -> r.getTipoMarcacion().equalsIgnoreCase("SALIDA")
                            && r.getEstado().equalsIgnoreCase("RECHAZADO"))
                    .findFirst()
                    .orElse(null);


            if (entrada != null && salida != null) {
                long horas = Duration.between(entrada.getFechaHora(), salida.getFechaHora()).toHours();

                diasCompletos++;

                // Calcular horas normales (máximo 8 horas)
                long horasNormales = Math.min(horas, HORAS_JORNADA_DIARIA);
                totalHorasNormales = totalHorasNormales.add(BigDecimal.valueOf(horasNormales));

                // Calcular horas extras (si trabajó más de 8 horas)
                if (horas > HORAS_JORNADA_DIARIA) {
                    long horasExtra = horas - HORAS_JORNADA_DIARIA;
                    totalHorasExtras = totalHorasExtras.add(BigDecimal.valueOf(horasExtra));
                }

                // Calcular horas NO trabajadas dentro de la jornada
                long horasNoTrabajadas = HORAS_JORNADA_DIARIA - horasNormales;
                if (horasNoTrabajadas > 0) {
                    totalHorasNoTrabajadas = totalHorasNoTrabajadas.add(BigDecimal.valueOf(horasNoTrabajadas));
                }
            }


            else if (entrada != null && salidaRechazada != null) {
                long horas = Duration.between(entrada.getFechaHora(), salidaRechazada.getFechaHora()).toHours();

                diasMedios++;

                // Pagar las horas que trabajó (hasta que se fue)
                long horasTrabajadas = Math.min(horas, HORAS_JORNADA_DIARIA);
                totalHorasNormales = totalHorasNormales.add(BigDecimal.valueOf(horasTrabajadas));

                // Descontar horas no trabajadas
                long horasNoTrabajadas = HORAS_JORNADA_DIARIA - horasTrabajadas;
                if (horasNoTrabajadas > 0) {
                    totalHorasSalidaTemprana = totalHorasSalidaTemprana.add(BigDecimal.valueOf(horasNoTrabajadas));
                    totalHorasNoTrabajadas = totalHorasNoTrabajadas.add(BigDecimal.valueOf(horasNoTrabajadas));
                }
            }

            else if (entradaRechazada != null && salida != null) {
                long horas = Duration.between(entradaRechazada.getFechaHora(), salida.getFechaHora()).toHours();

                diasMedios++;

                // Pagar las horas que trabajó (desde que llegó hasta que se fue)
                long horasTrabajadas = Math.min(horas, HORAS_JORNADA_DIARIA);
                totalHorasNormales = totalHorasNormales.add(BigDecimal.valueOf(horasTrabajadas));

                // Descontar horas no trabajadas (tardanza)
                long horasNoTrabajadas = HORAS_JORNADA_DIARIA - horasTrabajadas;
                if (horasNoTrabajadas > 0) {
                    totalHorasTardanza = totalHorasTardanza.add(BigDecimal.valueOf(horasNoTrabajadas));
                    totalHorasNoTrabajadas = totalHorasNoTrabajadas.add(BigDecimal.valueOf(horasNoTrabajadas));
                }
            }


            else if (entradaRechazada != null && salidaRechazada != null) {
                // No se paga nada (0 horas)
                totalHorasNoTrabajadas = totalHorasNoTrabajadas.add(BigDecimal.valueOf(HORAS_JORNADA_DIARIA));
            }

            else if (entrada != null && salida == null && salidaRechazada == null) {
                diasMedios++;

                // Se paga medio día (4 horas) por no marcar salida
                totalHorasNormales = totalHorasNormales.add(BigDecimal.valueOf(HORAS_JORNADA_DIARIA / 2));
                totalHorasNoTrabajadas = totalHorasNoTrabajadas.add(BigDecimal.valueOf(HORAS_JORNADA_DIARIA / 2));
            }



            else if (entrada == null && entradaRechazada == null && salida != null) {
                diasMedios++;

                // Se paga medio día (4 horas) por no marcar entrada
                totalHorasNormales = totalHorasNormales.add(BigDecimal.valueOf(HORAS_JORNADA_DIARIA / 2));
                totalHorasNoTrabajadas = totalHorasNoTrabajadas.add(BigDecimal.valueOf(HORAS_JORNADA_DIARIA / 2));
            }


            else {
                // No se paga nada
                totalHorasNoTrabajadas = totalHorasNoTrabajadas.add(BigDecimal.valueOf(HORAS_JORNADA_DIARIA));
            }
        }



        // Las horas extras se pagan con recargo:
        // - Primeras 2 horas: 25% adicional
        // - Horas siguientes: 35% adicional

        BigDecimal pagoHorasExtras = BigDecimal.ZERO;
        BigDecimal horasExtraCount = totalHorasExtras;

        if (horasExtraCount.compareTo(BigDecimal.ZERO) > 0) {
            // Calcular horas extras con recargo
            BigDecimal horasPrimeras2 = horasExtraCount.min(BigDecimal.valueOf(2));
            BigDecimal horasRestantes = horasExtraCount.subtract(horasPrimeras2);

            BigDecimal pagoPrimeras2 = tarifaHoraExtra25.multiply(horasPrimeras2);
            BigDecimal pagoRestantes = tarifaHoraExtra35.multiply(horasRestantes);

            pagoHorasExtras = pagoPrimeras2.add(pagoRestantes).setScale(2, RoundingMode.HALF_UP);
        }

        // CALCULAR TOTALES


        // Pago por horas normales trabajadas
        BigDecimal pagoHorasNormales = tarifaPorHora.multiply(totalHorasNormales)
                .setScale(2, RoundingMode.HALF_UP);

        // Descuento por horas no trabajadas
        BigDecimal descuentoHorasNoTrabajadas = tarifaPorHora.multiply(totalHorasNoTrabajadas)
                .setScale(2, RoundingMode.HALF_UP);


        // ASIGNACIÓN FAMILIAR


        BigDecimal asignacionFamiliar = empleado.getAsignacionFamiliar() != null && empleado.getAsignacionFamiliar()
                ? ASIGNACION_FAMILIAR_MONTO
                : BigDecimal.ZERO;


        // OTROS CONCEPTOS (Bonos y descuentos)


        BigDecimal bonoBeta = BigDecimal.ZERO;
        BigDecimal otrosBonos = BigDecimal.ZERO;
        BigDecimal otrosDescuentos = BigDecimal.ZERO;

        // TOTALES

        BigDecimal totalIngresos = pagoHorasNormales
                .add(pagoHorasExtras)
                .add(asignacionFamiliar)
                .add(bonoBeta)
                .add(otrosBonos);

        BigDecimal totalDescuentos = descuentoHorasNoTrabajadas
                .add(otrosDescuentos);

        BigDecimal neto = totalIngresos.subtract(totalDescuentos).setScale(2, RoundingMode.HALF_UP);
        if (neto.compareTo(BigDecimal.ZERO) < 0) {
            neto = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BoletaPagoRequestDTO dto = new BoletaPagoRequestDTO();
        dto.setEmpleadoId(empleadoId);
        dto.setSueldoBase(sueldoBase);
        dto.setDiasTrabajados(diasCompletos + diasMedios);
        dto.setDiasNoTrabajados(diasLaborablesCount - (diasCompletos + diasMedios));


        dto.setAsignacionFamiliar(asignacionFamiliar);
        dto.setBonoBeta(bonoBeta);
        dto.setHorasExtraPago(pagoHorasExtras);
        dto.setOtrosBonos(otrosBonos);
        dto.setDescuentoInasistencia(descuentoHorasNoTrabajadas);
        dto.setOtrosDescuentos(otrosDescuentos);
        dto.setTotalIngresos(totalIngresos);
        dto.setTotalDescuentos(totalDescuentos);
        dto.setNetoPagar(neto);

        return dto;
    }

    private boolean esDiaLaborable(LocalDate fecha) {
        switch (fecha.getDayOfWeek()) {
            case SATURDAY:
            case SUNDAY:
                return false;
            default:
                return true;
        }
    }
}