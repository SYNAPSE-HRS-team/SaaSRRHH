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

  obtenerAsistenciasHoy(): Observable<RegistroAsistenciaResponse[]> {
    return this.http.get<RegistroAsistenciaResponse[]>(`${this.apiUrl}/asistencias/hoy`);
  }

  obtenerIncidencias(): Observable<RegistroAsistenciaResponse[]> {
    return this.http.get<RegistroAsistenciaResponse[]>(`${this.apiUrl}/asistencias/incidencias`);
  }

  obtenerRankingTardanzas(): Observable<Array<[number, number]>> {
    return this.http.get<Array<[number, number]>>(`${this.apiUrl}/asistencias/ranking-tardanzas`);
  }
}