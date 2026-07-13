package com.SaasRRHH.main.services;

import com.SaasRRHH.main.DTO.AsistenciaCalendarioAnualDTO;
import com.SaasRRHH.main.DTO.AsistenciaCalendarioMesDTO;
import com.SaasRRHH.main.DTO.AsistenciaQrDTO;
import com.SaasRRHH.main.DTO.RegistroAsistenciaRequestDTO;
import com.SaasRRHH.main.DTO.RegistroAsistenciaResponseDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RegistroAsistenciaService {
    
    // CRUD Básico
    List<RegistroAsistenciaResponseDTO> listar();
    RegistroAsistenciaResponseDTO buscarPorId(Long id);
    RegistroAsistenciaResponseDTO guardar(RegistroAsistenciaRequestDTO dto);
    RegistroAsistenciaResponseDTO actualizar(Long id, RegistroAsistenciaRequestDTO dto);
    void eliminar(Long id);
    
    // Registro de asistencias
    RegistroAsistenciaResponseDTO registrarEntrada(Long empleadoId, String metodo);
    RegistroAsistenciaResponseDTO registrarSalida(Long empleadoId, String metodo);
    RegistroAsistenciaResponseDTO registrarPorQr(String payload);
    
    // QR
    AsistenciaQrDTO generarQrEmpleadoActual();
    
    // Calendario
    AsistenciaCalendarioMesDTO calendarioEmpleadoActual(Integer anio, Integer mes);
    AsistenciaCalendarioAnualDTO calendarioAnualEmpleadoActual(Integer anio);
    AsistenciaCalendarioMesDTO calendarioEmpleado(Long empleadoId, Integer anio, Integer mes);
    AsistenciaCalendarioAnualDTO calendarioAnualEmpleado(Long empleadoId, Integer anio);
    
    // Historial
    List<RegistroAsistenciaResponseDTO> historialEmpleadoActual();
    
    // Búsquedas
    List<RegistroAsistenciaResponseDTO> buscarPorEmpleado(Long empleadoId);
    List<RegistroAsistenciaResponseDTO> buscarPorEmpleadoYFecha(Long empleadoId, LocalDate fecha);
    List<RegistroAsistenciaResponseDTO> buscarPorEstado(String estado);
    List<RegistroAsistenciaResponseDTO> asistenciasPorFecha(LocalDate fecha);
    List<RegistroAsistenciaResponseDTO> asistenciasHoy();
    List<RegistroAsistenciaResponseDTO> incidenciasAsistencia();
    List<RegistroAsistenciaResponseDTO> listarCompleto();
    
    // Estadísticas
    Long contarAsistenciasMensuales(Long empleadoId, LocalDateTime inicio, LocalDateTime fin);
    List<Object[]> rankingTardanzas();
    boolean yaMarcoHoy(Long empleadoId, String tipo);
    
    // ============================================
    // ✅ NUEVOS MÉTODOS
    // ============================================
    
    /**
     * Procesa y marca faltas automáticas para todos los empleados que no registraron asistencia
     */
    void procesarFaltasAutomaticas();
    
    /**
     * Detecta patrones de tardanza para un empleado específico
     * @return String con el patrón detectado (ej: "LUNES_CONSECUTIVOS", "TENDENCIA_CRECIENTE", "MULTIPLE")
     */
    String detectarPatronTardanza(Long empleadoId);
}