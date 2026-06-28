import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseService } from './base.service';
import { EmpleadoRequest, EmpleadoResponse } from '../models/empleado.model';

@Injectable({ providedIn: 'root' })
export class EmpleadoService extends BaseService<EmpleadoRequest, EmpleadoResponse> {

  constructor(http: HttpClient) {
    super(http, 'empleados');
  }

  // ===================================
  // CONSULTAS ESPECIALIZADAS
  // ===================================

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
      params: { cargo, activo }
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
      params: { fechaLimite }
    });
  }

  contarPorCargo(): Observable<{ cargo: string; cantidad: number }[]> {
    return this.http.get<{ cargo: string; cantidad: number }[]>(`${this.baseUrl}/estadisticas/cargos`);
  }
}
