import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
    EncuestaBienestarRequest,
    EncuestaBienestarResponse,
} from '../models/encuesta-bienestar.model';

@Injectable({
  providedIn: 'root',
})
export class EncuestaBienestarService {
  private apiUrl = `${environment.apiUrl}/api/encuestas-bienestar`;

  constructor(private http: HttpClient) {}

  listar(): Observable<EncuestaBienestarResponse[]> {
    return this.http.get<EncuestaBienestarResponse[]>(this.apiUrl);
  }

  obtenerPorId(id: number): Observable<EncuestaBienestarResponse> {
    return this.http.get<EncuestaBienestarResponse>(`${this.apiUrl}/${id}`);
  }

  crear(dto: EncuestaBienestarRequest): Observable<EncuestaBienestarResponse> {
    return this.http.post<EncuestaBienestarResponse>(this.apiUrl, dto);
  }

  actualizar(id: number, dto: EncuestaBienestarRequest): Observable<EncuestaBienestarResponse> {
    return this.http.put<EncuestaBienestarResponse>(`${this.apiUrl}/${id}`, dto);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  historialEmpleado(empleadoId: number): Observable<EncuestaBienestarResponse[]> {
    return this.http.get<EncuestaBienestarResponse[]>(`${this.apiUrl}/empleado/${empleadoId}`);
  }

  obtenerResumen(inicio: string, fin: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/resumen`, {
      params: { inicio, fin },
    });
  }
}
