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
}