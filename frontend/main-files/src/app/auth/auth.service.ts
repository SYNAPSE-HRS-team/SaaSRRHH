// src/app/auth/auth.service.ts
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<any> {
    return this.http.post(this.apiUrl + '/login', { email, password }).pipe(
      tap((response: any) => {
        if (response.token) {
          localStorage.setItem('token', response.token);
        }
        if (response.email) {
          localStorage.setItem('email', response.email);
        }
        if (response.roles) {
          const roles = response.roles.map((role: string) => role.startsWith('ROLE_') ? role : 'ROLE_' + role);
          localStorage.setItem('roles', JSON.stringify(roles));
        }
      }),
    );
  }

  setToken(token: string): void { localStorage.setItem('token', token); }

  getToken(): string | null { return localStorage.getItem('token'); }

  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) return false;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const exp = payload.exp * 1000;
      return Date.now() < exp;
    } catch (e) {
      return false;
    }
  }

  logout(): void {
    localStorage.clear();
    sessionStorage.clear();
  }

  getEmail(): string | null { return localStorage.getItem('email'); }

  getRoles(): string[] {
    const roles = localStorage.getItem('roles');
    return roles ? JSON.parse(roles) : [];
  }

  hasRole(...roles: string[]): boolean {
    const currentRoles = this.getRoles().map((role) => this.normalizeRole(role));
    const expectedRoles = roles.map((role) => this.normalizeRole(role));
    return expectedRoles.some((role) => currentRoles.includes(role));
  }

  isEmpleado(): boolean { return this.hasRole('EMPLEADO', 'OBRERO', 'ROLE_EMPLEADO', 'ROLE_OBRERO'); }

  isSupervisor(): boolean { return this.hasRole('SUPERVISOR', 'ROLE_SUPERVISOR'); }

  isAdmin(): boolean { return this.hasRole('ADMIN', 'ROLE_ADMIN'); }

  getPrimaryRoute(): string {
    if (this.isEmpleado() && !this.isAdmin() && !this.isSupervisor()) {
      return '/dashboard/mi-asistencia';
    }
    return '/dashboard/human-resources';
  }

  private normalizeRole(role: string): string {
    return (role || '').replace(/^ROLE_/, '').toUpperCase();
  }
}
