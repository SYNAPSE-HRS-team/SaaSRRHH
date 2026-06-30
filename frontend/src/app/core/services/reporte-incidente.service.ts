import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

import {
    ReporteIncidenteRequest,
    ReporteIncidenteResponse,
} from '../models/reporte-incidente.model';

@Injectable({
  providedIn: 'root',
})
export class ReporteIncidenteService {
  private apiUrl = `${environment.apiUrl}/api/reportes-incidentes`;

  constructor(private http: HttpClient) {}

  listar(): Observable<ReporteIncidenteResponse[]> {
    console.log('URL de listar:', this.apiUrl); // ← Agrega esto
    return this.http.get<ReporteIncidenteResponse[]>(this.apiUrl);
  }

  obtenerPorId(id: number): Observable<ReporteIncidenteResponse> {
    return this.http.get<ReporteIncidenteResponse>(`${this.apiUrl}/${id}`);
  }

  crear(dto: ReporteIncidenteRequest): Observable<ReporteIncidenteResponse> {
    return this.http.post<ReporteIncidenteResponse>(this.apiUrl, dto);
  }

  actualizar(id: number, dto: ReporteIncidenteRequest): Observable<ReporteIncidenteResponse> {
    return this.http.put<ReporteIncidenteResponse>(`${this.apiUrl}/${id}`, dto);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  listarCriticos(): Observable<ReporteIncidenteResponse[]> {
    return this.http.get<ReporteIncidenteResponse[]>(`${this.apiUrl}/criticos`);
  }

  listarDeHoy(): Observable<ReporteIncidenteResponse[]> {
    return this.http.get<ReporteIncidenteResponse[]>(`${this.apiUrl}/hoy`);
  }
}
