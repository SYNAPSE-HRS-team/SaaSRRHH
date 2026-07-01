import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseService } from './base.service';
import { DocumentoPrivadoRequest, DocumentoPrivadoResponse } from '../models/documento-privado.model';

@Injectable({ providedIn: 'root' })
export class DocumentoService extends BaseService<DocumentoPrivadoRequest, DocumentoPrivadoResponse> {
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
}