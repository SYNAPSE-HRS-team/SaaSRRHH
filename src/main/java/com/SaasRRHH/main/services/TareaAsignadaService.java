package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.TareaAsignada;
import com.SaasRRHH.main.model.TareaAsignada.EstadoTarea;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TareaAsignadaService {

    List<TareaAsignada> listar();

    Optional<TareaAsignada> buscarPorId(Long id);

    TareaAsignada guardar(TareaAsignada tarea);

    Optional<TareaAsignada> actualizar(Long id, TareaAsignada tarea);

    void eliminar(Long id);

    List<TareaAsignada> buscarPorEmpleado(Long empleadoId);

    List<TareaAsignada> buscarPorSupervisor(Long supervisorId);

    List<TareaAsignada> buscarPorEstado(EstadoTarea estado);

    List<TareaAsignada> buscarPorEmpleadoYFecha(Long empleadoId, LocalDate fecha);

    TareaAsignada cambiarEstado(Long id, EstadoTarea nuevoEstado);
}