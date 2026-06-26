// src/app/application/rrhh/usuarios/usuarios.service.ts
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UsuariosService {
  private apiUrl = 'http://localhost:8080/api/usuarios';

  constructor(private http: HttpClient) {}

  // ========== CRUD BÁSICO ==========

  // GET /api/usuarios - Listar todos
  listar(): Observable<any> {
    return this.http.get(this.apiUrl);
  }

  // GET /api/usuarios/{id} - Obtener por ID
  obtener(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

  // POST /api/usuarios - Crear usuario
  crear(usuario: any): Observable<any> {
    return this.http.post(this.apiUrl, usuario);
  }

  // DELETE /api/usuarios/{id} - Eliminar
  eliminar(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  // ========== CONSULTAS ESPECIALES ==========

  // GET /api/usuarios/email/{email} - Buscar por email
  buscarPorEmail(email: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/email/${email}`);
  }

  // GET /api/usuarios/activos - Listar activos
  listarActivos(): Observable<any> {
    return this.http.get(`${this.apiUrl}/activos`);
  }

  // GET /api/usuarios/rol/{rol} - Buscar por rol
  buscarPorRol(rol: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/rol/${rol}`);
  }

  // GET /api/usuarios/acceso-reciente - Acceso reciente
  accesoReciente(fecha: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/acceso-reciente?fecha=${fecha}`);
  }

  // GET /api/usuarios/estadisticas/roles - Estadísticas
  estadisticasPorRol(): Observable<any> {
    return this.http.get(`${this.apiUrl}/estadisticas/roles`);
  }

  // PATCH /api/usuarios/{id}/ultimo-acceso - Actualizar último acceso
  actualizarUltimoAcceso(id: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}/ultimo-acceso`, {});
  }
}
