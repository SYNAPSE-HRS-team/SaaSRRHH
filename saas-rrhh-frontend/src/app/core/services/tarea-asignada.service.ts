import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TareaAsignadaRequest, TareaAsignadaResponse } from '../models/tarea-asignada.model';

@Injectable({
  providedIn: 'root',
})
export class TareaAsignadaService {
  private apiUrl = `${environment.apiUrl}/api/tareas-asignadas`;

  constructor(private http: HttpClient) {}

  listar(): Observable<TareaAsignadaResponse[]> {
    return this.http.get<TareaAsignadaResponse[]>(this.apiUrl);
  }

  getTareasByEmpleado(empleadoId: number): Observable<TareaAsignadaResponse[]> {
    return this.http.get<TareaAsignadaResponse[]>(`${this.apiUrl}/empleado/${empleadoId}`);
  }

  obtenerPorId(id: number): Observable<TareaAsignadaResponse> {
    return this.http.get<TareaAsignadaResponse>(`${this.apiUrl}/${id}`);
  }

  crear(dto: TareaAsignadaRequest): Observable<TareaAsignadaResponse> {
    return this.http.post<TareaAsignadaResponse>(this.apiUrl, dto);
  }

  actualizar(id: number, dto: TareaAsignadaRequest): Observable<TareaAsignadaResponse> {
    return this.http.put<TareaAsignadaResponse>(`${this.apiUrl}/${id}`, dto);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  cambiarEstado(id: number, estado: string): Observable<TareaAsignadaResponse> {
    return this.http.patch<TareaAsignadaResponse>(`${this.apiUrl}/${id}/estado`, null, {
      params: { estado },
    });
  }
  marcarVencidas(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/marcar-vencidas`, {});
  }
}
