import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ReporteDiarioRequest, ReporteDiarioResponse } from '../models/reporte-diario.model';

@Injectable({
  providedIn: 'root',
})
export class ReporteDiarioService {
  private apiUrl = `${environment.apiUrl}/api/reportes-diarios`;

  constructor(private http: HttpClient) {}

  listar(): Observable<ReporteDiarioResponse[]> {
    return this.http.get<ReporteDiarioResponse[]>(this.apiUrl);
  }

  obtenerPorId(id: number): Observable<ReporteDiarioResponse> {
    return this.http.get<ReporteDiarioResponse>(`${this.apiUrl}/${id}`);
  }

  crear(dto: ReporteDiarioRequest): Observable<ReporteDiarioResponse> {
    return this.http.post<ReporteDiarioResponse>(this.apiUrl, dto);
  }

  actualizar(id: number, dto: ReporteDiarioRequest): Observable<ReporteDiarioResponse> {
    return this.http.put<ReporteDiarioResponse>(`${this.apiUrl}/${id}`, dto);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getReportesByEmpleado(empleadoId: number): Observable<ReporteDiarioResponse[]> {
    return this.http.get<ReporteDiarioResponse[]>(`${this.apiUrl}/empleado/${empleadoId}`);
  }

  getReportesByTarea(tareaId: number): Observable<ReporteDiarioResponse[]> {
    return this.http.get<ReporteDiarioResponse[]>(`${this.apiUrl}/tarea/${tareaId}`);
  }
}
