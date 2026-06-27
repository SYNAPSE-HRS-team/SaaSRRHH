import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface DashboardResumen {
  totalEmpleados: number;
  totalUsuarios: number;
  reportesDiarios: number;
  ausencias: number;
  incidentes: number;
  porcentajeAusentismo: number;
  nivelRiesgo: string;
}

export interface EmpleadoResponse {
  id: number;
  nombres: string;
  apellidos: string;
  cargo?: string | null;
  areaNombre?: string | null;
  email?: string | null;
  activo?: boolean | null;
}

export interface RegistroAsistenciaResponse {
  id: number;
  empleadoId: number;
  dispositivoId?: number | null;
  fechaHora: string;
  tipoMarcacion: string;
  metodo: string;
  estado: string;
  observaciones?: string | null;
}

export interface ValidacionSeguridadRequest {
  asistenciaId?: number | null;
  dispositivoId?: number | null;
  totpHash: string;
  totpValido: boolean;
}

export interface ValidacionSeguridadResponse {
  id: number;
  asistenciaId?: number | null;
  dispositivoId?: number | null;
  totpHash: string;
  totpValido: boolean;
  fechaValidacion: string;
}

export interface RankingTardanza {
  empleadoId: number;
  tardanzas: number;
}

@Injectable({
  providedIn: 'root',
})
export class AttendanceDashboardService {
  private readonly apiUrl = 'http://localhost:8080/api';

  constructor(private readonly http: HttpClient) {}

  obtenerDashboard(): Observable<DashboardResumen> {
    return this.http.get<DashboardResumen>(`${this.apiUrl}/analitica/dashboard`);
  }

  obtenerEmpleados(): Observable<EmpleadoResponse[]> {
    return this.http.get<EmpleadoResponse[]>(`${this.apiUrl}/empleados`);
  }

  obtenerAsistenciasHoy(): Observable<RegistroAsistenciaResponse[]> {
    return this.http.get<RegistroAsistenciaResponse[]>(`${this.apiUrl}/asistencias/hoy`);
  }

  obtenerValidacionesSeguridad(): Observable<ValidacionSeguridadResponse[]> {
    return this.http.get<ValidacionSeguridadResponse[]>(`${this.apiUrl}/validaciones-seguridad`);
  }

  crearValidacionSeguridad(payload: ValidacionSeguridadRequest): Observable<ValidacionSeguridadResponse> {
    return this.http.post<ValidacionSeguridadResponse>(`${this.apiUrl}/validaciones-seguridad`, payload);
  }

  obtenerIncidencias(): Observable<RegistroAsistenciaResponse[]> {
    return this.http.get<RegistroAsistenciaResponse[]>(`${this.apiUrl}/asistencias/incidencias`);
  }

  obtenerRankingTardanzas(): Observable<Array<[number, number]>> {
    return this.http.get<Array<[number, number]>>(`${this.apiUrl}/asistencias/ranking-tardanzas`);
  }

  registrarEntrada(empleadoId: number, metodo = 'QR'): Observable<RegistroAsistenciaResponse> {
    return this.http.post<RegistroAsistenciaResponse>(
      `${this.apiUrl}/asistencias/entrada/${empleadoId}?metodo=${encodeURIComponent(metodo)}`,
      {},
    );
  }

  registrarSalida(empleadoId: number, metodo = 'QR'): Observable<RegistroAsistenciaResponse> {
    return this.http.post<RegistroAsistenciaResponse>(
      `${this.apiUrl}/asistencias/salida/${empleadoId}?metodo=${encodeURIComponent(metodo)}`,
      {},
    );
  }
}