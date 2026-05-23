package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.RegistroAsistencia;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RegistroAsistenciaService {

    List<RegistroAsistencia> listar();

    Optional<RegistroAsistencia> buscarPorId(Long id);

    RegistroAsistencia guardar(RegistroAsistencia registroAsistencia);

    RegistroAsistencia registrarEntrada(Long empleadoId, String metodo);

    RegistroAsistencia registrarSalida(Long empleadoId, String metodo);

    void eliminar(Long id);

    List<RegistroAsistencia> buscarPorEmpleado(Long empleadoId);

    List<RegistroAsistencia> buscarPorEmpleadoYFecha(Long empleadoId, LocalDate fecha);

    List<RegistroAsistencia> buscarPorEstado(String estado);
}