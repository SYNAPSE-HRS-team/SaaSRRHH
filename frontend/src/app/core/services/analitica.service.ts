import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DashboardDTO {
  totalEmpleados: number;
  totalUsuarios: number;
  reportesDiarios: number;
  ausencias: number;
  incidentes: number;
  porcentajeAusentismo: number;
  nivelRiesgo: string;
}

@Injectable({ providedIn: 'root' })
export class AnaliticaService {
  private baseUrl = '/api/analitica';

  constructor(private http: HttpClient) {}

  obtenerDashboard(): Observable<DashboardDTO> {
    return this.http.get<DashboardDTO>(`${this.baseUrl}/dashboard`);
  }
}
