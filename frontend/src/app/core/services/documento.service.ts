import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  DocumentoPrivadoRequest,
  DocumentoPrivadoResponse,
} from '../models/documento-privado.model';
import { BaseService } from './base.service';

@Injectable({ providedIn: 'root' })
export class DocumentoService extends BaseService<
  DocumentoPrivadoRequest,
  DocumentoPrivadoResponse
> {
  constructor(http: HttpClient) {
    super(http, 'documentos-privados');
  }

  subirArchivo(file: File): Observable<{ url: string }> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{ url: string }>(`${this.baseUrl}/upload`, formData);
  }

  buscarPorEmpleado(empleadoId: number): Observable<DocumentoPrivadoResponse[]> {
    return this.http.get<DocumentoPrivadoResponse[]>(`${this.baseUrl}/empleado/${empleadoId}`);
  }

  buscarPorTipo(tipoId: number): Observable<DocumentoPrivadoResponse[]> {
    return this.http.get<DocumentoPrivadoResponse[]>(`${this.baseUrl}/tipo/${tipoId}`);
  }

  listarActivos(): Observable<DocumentoPrivadoResponse[]> {
    return this.http.get<DocumentoPrivadoResponse[]>(`${this.baseUrl}/activos`);
  }

  buscarPorFechaEmision(fecha: string): Observable<DocumentoPrivadoResponse[]> {
    const params = new HttpParams().set('fecha', fecha);
    // Ojo: Tu backend tiene "buscar_fecha_emision/" con un slash al final
    return this.http.get<DocumentoPrivadoResponse[]>(`${this.baseUrl}/buscar_fecha_emision/`, {
      params,
    });
  }

  /**
   * Trae la lista de todos los documentos que ya caducaron
   */
  listarVencidos(): Observable<DocumentoPrivadoResponse[]> {
    return this.http.get<DocumentoPrivadoResponse[]>(`${this.baseUrl}/vencidos`);
  }

  /**
   * Trae los documentos que van a vencer antes de la fecha límite enviada
   */
  listarPorVencer(fechaLimite: string): Observable<DocumentoPrivadoResponse[]> {
    const params = new HttpParams().set('fechaLimite', fechaLimite);
    return this.http.get<DocumentoPrivadoResponse[]>(`${this.baseUrl}/por-vencer`, { params });
  }

  /**
   * Trae estadísticas de conteo agrupadas por tipo de documento (DNI, Contratos, etc.)
   */
  obtenerEstadisticasPorTipo(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/estadisticas/tipos`);
  }

  /**
   * Trae el ranking de empleados que registran más documentos en el sistema
   */
  obtenerEstadisticasPorEmpleado(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/estadisticas/empleados`);
  }
}
