// src/app/auth/auth.service.ts
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  // Ajusta la URL según tu backend
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  // ========== MÉTODO DE LOGIN ==========
  // Recibe email y password como parámetros separados
  login(email: string, password: string): Observable<any> {
    // Envía el objeto con email y password
    return this.http.post(`${this.apiUrl}/login`, { email, password }).pipe(
      tap((response: any) => {
        // Guardar el token si existe
        if (response.token) {
          localStorage.setItem('token', response.token);
        }
        // Guardar email si existe
        if (response.email) {
          localStorage.setItem('email', response.email);
        }
        // Guardar roles si existen
        if (response.roles) {
          localStorage.setItem('roles', JSON.stringify(response.roles));
        }
      }),
    );
  }

  // ========== MÉTODOS PARA TOKEN ==========

  // Guardar token
  setToken(token: string): void {
    localStorage.setItem('token', token);
  }

  // Obtener token
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // Verificar si está autenticado
  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) return false;

    // Opcional: Verificar si el token ha expirado
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const exp = payload.exp * 1000; // Convertir a milisegundos
      return Date.now() < exp; // Token válido si no ha expirado
    } catch (e) {
      return false; // Si hay error, considerar no autenticado
    }
  }

  // Cerrar sesión
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('email');
    localStorage.removeItem('roles');
  }

  // Obtener email del usuario
  getEmail(): string | null {
    return localStorage.getItem('email');
  }

  // Obtener roles del usuario
  getRoles(): string[] | null {
    const roles = localStorage.getItem('roles');
    return roles ? JSON.parse(roles) : null;
  }
}
