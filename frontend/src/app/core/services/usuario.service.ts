import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { UsuarioRequest, UsuarioResponse } from '../models/usuario.model';

@Injectable({
  providedIn: 'root',
})
export class UsuarioService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/usuarios';

  listar(): Observable<UsuarioResponse[]> {
    console.log('🔍 Frontend: Llamando a listar()');
    console.log('🔍 URL:', this.apiUrl);
    return this.http.get<UsuarioResponse[]>(this.apiUrl);
  }

  obtener(id: number): Observable<UsuarioResponse> {
    return this.http.get<UsuarioResponse>(`${this.apiUrl}/${id}`);
  }

  guardar(dto: UsuarioRequest): Observable<UsuarioResponse> {
    return this.http.post<UsuarioResponse>(this.apiUrl, dto);
  }

  // ✅ MÉTODO ACTUALIZAR (faltaba)
  actualizar(id: number, data: Partial<UsuarioRequest>): Observable<UsuarioResponse> {
    return this.http.put<UsuarioResponse>(`${this.apiUrl}/${id}`, data);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  listarActivos(): Observable<UsuarioResponse[]> {
    return this.http.get<UsuarioResponse[]>(`${this.apiUrl}/activos`);
  }

  listarSinEmpleado(): Observable<UsuarioResponse[]> {
    return this.http.get<UsuarioResponse[]>(`${this.apiUrl}/sin-empleado`);
  }

  obtenerPorEmail(email: string): Observable<UsuarioResponse> {
    return this.http.get<UsuarioResponse>(`${this.apiUrl}/email/${email}`);
  }

  getProfile(): Observable<UsuarioResponse> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.get<UsuarioResponse>(`${this.apiUrl}/profile`, { headers });
  }

  actualizarPerfil(id: number, data: any): Observable<UsuarioResponse> {
    return this.http.put<UsuarioResponse>(`${this.apiUrl}/${id}/profile`, data);
  }
}