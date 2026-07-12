import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface EmpleadoAlertaDTO {
  empleadoId: number;
  nombreEmpleado: string;
  cargo: string;
  nivelRiesgo: string;
  indicePuntualidad: number;
  faltasMes: number;
  tardanzasMes: number;
  patronDetectado: string;
}

export interface DashboardDTO {
  totalEmpleados: number;
  totalUsuarios: number;
  reportesDiarios: number;
  ausencias: number;
  incidentes: number;
  porcentajeAusentismo: number;
  nivelRiesgo: string;
  // ✅ NUEVOS CAMPOS
  empleadosRiesgoAlto?: number;
  totalAlertas?: number;
  promedioPuntualidad?: number;
  totalFaltasHoy?: number;
  totalTardanzasHoy?: number;
  feedbackPendientes?: number;
  rankingBajoDesempeno?: EmpleadoAlertaDTO[];
}

@Injectable({ providedIn: 'root' })
export class AnaliticaService {
  private baseUrl = '/api/analitica';

  constructor(private http: HttpClient) {}

  obtenerDashboard(): Observable<DashboardDTO> {
    return this.http.get<DashboardDTO>(`${this.baseUrl}/dashboard`);
  }

  // ✅ NUEVO: Obtener ranking de bajo desempeño
  obtenerRankingDesempeno(): Observable<EmpleadoAlertaDTO[]> {
    return this.http.get<EmpleadoAlertaDTO[]>(`${this.baseUrl}/ranking-desempeno`);
  }

  // ✅ NUEVO: Obtener alertas activas
  obtenerAlertas(): Observable<any> {
    return this.http.get(`${this.baseUrl}/alertas`);
  }

  // ✅ NUEVO: Obtener métricas de puntualidad
  obtenerPuntualidad(): Observable<any> {
    return this.http.get(`${this.baseUrl}/puntualidad`);
  }

  // ✅ NUEVO: Resumen completo
  obtenerResumenCompleto(): Observable<DashboardDTO> {
    return this.http.get<DashboardDTO>(`${this.baseUrl}/resumen-completo`);
  }
}