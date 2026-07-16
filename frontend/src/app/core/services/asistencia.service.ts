import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  AsistenciaQr,
  CalendarioAnual,
  CalendarioMes,
  RegistroAsistencia,
} from '../models/registro-asistencia.model';

@Injectable({ providedIn: 'root' })
export class AsistenciaService {
  private baseUrl = `${environment.apiUrl}/api/asistencias`;

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
    return this.http.get<CalendarioAnual>(`${this.baseUrl}/mi-calendario/anual`, {
      params: { anio },
    });
  }

  calendarioEmpleado(empleadoId: number, anio: number, mes: number): Observable<CalendarioMes> {
    return this.http.get<CalendarioMes>(`${this.baseUrl}/calendario/${empleadoId}`, {
      params: { anio, mes },
    });
  }

  calendarioAnualEmpleado(empleadoId: number, anio: number): Observable<CalendarioAnual> {
    return this.http.get<CalendarioAnual>(`${this.baseUrl}/calendario/${empleadoId}/anual`, {
      params: { anio },
    });
  }

  asistenciasHoy(fecha?: string): Observable<RegistroAsistencia[]> {
    const params: any = {};
    if (fecha) params.fecha = fecha;
    return this.http.get<RegistroAsistencia[]>(`${this.baseUrl}/hoy`, { params });
  }

  miHistorial(): Observable<RegistroAsistencia[]> {
    return this.http.get<RegistroAsistencia[]>(`${this.baseUrl}/mi-historial`);
  }

  historialEmpleado(empleadoId: number): Observable<RegistroAsistencia[]> {
    return this.http.get<RegistroAsistencia[]>(`${this.baseUrl}/empleado/${empleadoId}`);
  }

  registrarEntrada(empleadoId: number, metodo?: string): Observable<RegistroAsistencia> {
    const params: any = {};
    if (metodo) params.metodo = metodo;
    return this.http.post<RegistroAsistencia>(`${this.baseUrl}/entrada/${empleadoId}`, null, {
      params,
    });
  }

  registrarSalida(empleadoId: number, metodo?: string): Observable<RegistroAsistencia> {
    const params: any = {};
    if (metodo) params.metodo = metodo;
    return this.http.post<RegistroAsistencia>(`${this.baseUrl}/salida/${empleadoId}`, null, {
      params,
    });
  }

  crear(data: RegistroAsistencia): Observable<RegistroAsistencia> {
    return this.http.post<RegistroAsistencia>(this.baseUrl, data);
  }

  actualizar(id: number, data: RegistroAsistencia): Observable<RegistroAsistencia> {
    return this.http.put<RegistroAsistencia>(`${this.baseUrl}/${id}`, data);
  }

  // ✅ NUEVO: Procesar faltas automáticas (admin)
  procesarFaltas(): Observable<any> {
    return this.http.post(`${this.baseUrl}/procesar-faltas`, {});
  }

  // ✅ NUEVO: Detectar patrón de tardanza de un empleado
  detectarPatronTardanza(
    empleadoId: number,
  ): Observable<{ empleadoId: string; patronDetectado: string }> {
    return this.http.get<{ empleadoId: string; patronDetectado: string }>(
      `${this.baseUrl}/empleado/${empleadoId}/patron-tardanza`,
    );
  }

  // ✅ NUEVO: Obtener estadísticas de asistencia
  obtenerEstadisticas(empleadoId: number, inicio: string, fin: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/empleado/${empleadoId}/estadisticas`, {
      params: { inicio, fin },
    });
  }
}
