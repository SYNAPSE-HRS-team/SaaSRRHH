import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AsistenciaQr, CalendarioAnual, CalendarioMes, RegistroAsistencia } from '../models/registro-asistencia.model';

@Injectable({ providedIn: 'root' })
export class AsistenciaService {
  private baseUrl = '/api/asistencias';

  constructor(private http: HttpClient) {}

  miQr(): Observable<AsistenciaQr> {
    return this.http.get<AsistenciaQr>(`${this.baseUrl}/mi-qr`);
  }

  scanQr(payload: string): Observable<RegistroAsistencia> {
    return this.http.post<RegistroAsistencia>(`${this.baseUrl}/scan-qr`, { payload });
  }

  miCalendario(anio: number, mes: number): Observable<CalendarioMes> {
    return this.http.get<CalendarioMes>(`${this.baseUrl}/mi-calendario`, { params: { anio, mes } });
  }

  miCalendarioAnual(anio: number): Observable<CalendarioAnual> {
    return this.http.get<CalendarioAnual>(`${this.baseUrl}/mi-calendario/anual`, { params: { anio } });
  }

  calendarioEmpleado(empleadoId: number, anio: number, mes: number): Observable<CalendarioMes> {
    return this.http.get<CalendarioMes>(`${this.baseUrl}/calendario/${empleadoId}`, { params: { anio, mes } });
  }

  calendarioAnualEmpleado(empleadoId: number, anio: number): Observable<CalendarioAnual> {
    return this.http.get<CalendarioAnual>(`${this.baseUrl}/calendario/${empleadoId}/anual`, { params: { anio } });
  }

  asistenciasHoy(): Observable<RegistroAsistencia[]> {
    return this.http.get<RegistroAsistencia[]>(`${this.baseUrl}/hoy`);
  }

  crear(data: RegistroAsistencia): Observable<RegistroAsistencia> {
    return this.http.post<RegistroAsistencia>(this.baseUrl, data);
  }

  actualizar(id: number, data: RegistroAsistencia): Observable<RegistroAsistencia> {
    return this.http.put<RegistroAsistencia>(`${this.baseUrl}/${id}`, data);
  }
}
