import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EmpleadoRequest, EmpleadoResponse } from '../models/empleado.model';
import { BaseService } from './base.service';

@Injectable({ providedIn: 'root' })
export class EmpleadoService extends BaseService<EmpleadoRequest, EmpleadoResponse> {
  constructor(http: HttpClient) {
    super(http, 'empleados');
  }

  buscarPorDni(dni: string): Observable<EmpleadoResponse> {
    return this.http.get<EmpleadoResponse>(`${this.baseUrl}/dni/${dni}`);
  }

  listarActivos(): Observable<EmpleadoResponse[]> {
    return this.http.get<EmpleadoResponse[]>(`${this.baseUrl}/activos`);
  }

  buscarPorCargo(cargo: string): Observable<EmpleadoResponse[]> {
    return this.http.get<EmpleadoResponse[]>(`${this.baseUrl}/cargo/${cargo}`);
  }

  buscarPorCargoYActivo(cargo: string, activo: boolean): Observable<EmpleadoResponse[]> {
    return this.http.get<EmpleadoResponse[]>(`${this.baseUrl}/cargo-activo`, {
      params: { cargo, activo },
    });
  }

  listarActivosConUsuario(): Observable<EmpleadoResponse[]> {
    return this.http.get<EmpleadoResponse[]>(`${this.baseUrl}/activos-usuario`);
  }

  contratosVencidos(): Observable<EmpleadoResponse[]> {
    return this.http.get<EmpleadoResponse[]>(`${this.baseUrl}/contratos-vencidos`);
  }

  contratosPorVencer(fechaLimite: string): Observable<EmpleadoResponse[]> {
    return this.http.get<EmpleadoResponse[]>(`${this.baseUrl}/contratos-por-vencer`, {
      params: { fechaLimite },
    });
  }

  contarPorCargo(): Observable<{ cargo: string; cantidad: number }[]> {
    return this.http.get<{ cargo: string; cantidad: number }[]>(
      `${this.baseUrl}/estadisticas/cargos`,
    );
  }

  listarSupervisores(): Observable<EmpleadoResponse[]> {
    return this.http.get<EmpleadoResponse[]>(`${this.baseUrl}/supervisores`);
  }

  listarTrabajadores(): Observable<EmpleadoResponse[]> {
    return this.http.get<EmpleadoResponse[]>(`${this.baseUrl}/trabajadores`);
  }

  listarTrabajadoresByRol(): Observable<EmpleadoResponse[]> {
    return this.http.get<EmpleadoResponse[]>(`${this.baseUrl}/trabajadores-rol`);
  }

  listarSupervisoresByRol(): Observable<EmpleadoResponse[]> {
    return this.http.get<EmpleadoResponse[]>(`${this.baseUrl}/supervisores-rol`);
  }

  // ===================================
  // NUEVO MÉTODO PARA PERFIL
  // ===================================

  buscarPorUsuarioId(usuarioId: number): Observable<EmpleadoResponse> {
    return this.http.get<EmpleadoResponse>(`${this.baseUrl}/usuario/${usuarioId}`);
  }

  // Agregar estos métodos a tu EmpleadoService existente:

  /**
   * ✅ Obtener resumen de puntualidad de un empleado
   */
  obtenerResumenPuntualidad(id: number): Observable<EmpleadoResponse> {
    return this.http.get<EmpleadoResponse>(`${this.baseUrl}/${id}/resumen-puntualidad`);
  }

  /**
   * ✅ Calcular horas contrato vs reales
   */
  calcularHorasContrato(id: number, inicio: string, fin: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/${id}/horas-contrato`, { params: { inicio, fin } });
  }

  /**
   * ✅ Actualizar solo el horario de un empleado
   */
  actualizarHorario(id: number, horario: any): Observable<EmpleadoResponse> {
    return this.http.patch<EmpleadoResponse>(`${this.baseUrl}/${id}/horario`, horario);
  }

  /**
   * ✅ Actualizar tipo de pago
   */
  actualizarTipoPago(id: number, tipoPago: any): Observable<EmpleadoResponse> {
    return this.http.patch<EmpleadoResponse>(`${this.baseUrl}/${id}/tipo-pago`, tipoPago);
  }
}